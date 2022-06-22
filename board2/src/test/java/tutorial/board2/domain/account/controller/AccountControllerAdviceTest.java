package tutorial.board2.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.exception.LoginFailureException;
import tutorial.board2.domain.account.exception.MemberNicknameAlreadyExistsException;
import tutorial.board2.domain.account.exception.RoleNotFoundException;
import tutorial.board2.domain.account.service.AccountService;
import tutorial.board2.global.advice.ExceptionAdvice;
import tutorial.board2.global.exception.AuthenticationEntryPointException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerAdviceTest {
    @InjectMocks AccountController accountController;
    @Mock AccountService accountService;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).setControllerAdvice(new ExceptionAdvice(messageSource)).build();
    }

    @Test
    void signInLoginFailureExceptionTest() throws Exception {
        // given
        SignInRequest req = new SignInRequest("username", "123456a!");
        given(accountService.signIn(any())).willThrow(LoginFailureException.class);

        // when, then
        mockMvc.perform(
                        post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signInMethodArgumentNotValidExceptionTest() throws Exception {
        // given
        SignInRequest req = new SignInRequest(" ", "1234567");

        // when, then
        mockMvc.perform(
                        post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signUpMemberNicknameAlreadyExistsExceptionTest() throws Exception {
        // given
        SignUpRequest req = new SignUpRequest("username", "123456a!",  "nickname");
        doThrow(MemberNicknameAlreadyExistsException.class).when(accountService).signUp(any());

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void signUpRoleNotFoundExceptionTest() throws Exception {
        // given
        SignUpRequest req = new SignUpRequest("username", "123456a!", "nickname");
        doThrow(RoleNotFoundException.class).when(accountService).signUp(any());

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void signUpMethodArgumentNotValidExceptionTest() throws Exception {
        // given
        SignUpRequest req = new SignUpRequest("", "", "");

        // when, then
        mockMvc.perform(
                        post("/api/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void refreshTokenAuthenticationEntryPointException() throws Exception {
        // given
        given(accountService.refreshAccessToken(anyString())).willThrow(AuthenticationEntryPointException.class);

        // when, then
        mockMvc.perform(
                        post("/api/refresh-token")
                                .header("Authorization", "refreshToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(-1001));
    }

    @Test
    void refreshTokenMissingRequestHeaderException() throws Exception {
        // given, when, then
        mockMvc.perform(
                        post("/api/refresh-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(-1009));
    }
}