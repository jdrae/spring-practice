package tutorial.board2.domain.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignInResponse;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.exception.LoginFailureException;
import tutorial.board2.domain.account.exception.MemberNicknameAlreadyExistsException;
import tutorial.board2.domain.account.exception.MemberUsernameAlreadyExistsException;
import tutorial.board2.domain.account.exception.RoleNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;
import tutorial.board2.domain.account.repository.RoleRepository;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks AccountService accountService;
    @Mock MemberRepository memberRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenService tokenService;

    /*
    * 회원가입
    * */
    @Test
    void signUpTest() {
        // given
        SignUpRequest req = createSignUpRequest();
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.of(new Role(RoleType.ROLE_NORMAL)));

        // when
        accountService.signUp(req);

        // then
        verify(passwordEncoder).encode(req.getPassword());
        verify(memberRepository).save(any());
    }

    @Test
    void validateSignUpByDuplicateUsernameTest() {
        // given
        given(memberRepository.existsByUsername(anyString())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> accountService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberUsernameAlreadyExistsException.class);
    }

    @Test
    void validateSignUpByDuplicateNicknameTest() {
        // given
        given(memberRepository.existsByNickname(anyString())).willReturn(true);

        // when, then
        assertThatThrownBy(() -> accountService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberNicknameAlreadyExistsException.class);
    }

    @Test
    void signUpRoleNotFoundTest() {
        // 등록되지 않은 권한 등급으로 회원가입 수행
        // given
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> accountService.signUp(createSignUpRequest()))
                .isInstanceOf(RoleNotFoundException.class);
    }

    /*
    * 로그인
    * */
    @Test
    void signInTest() {
        // given
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(tokenService.createAccessToken(anyString())).willReturn("access");
        given(tokenService.createRefreshToken(anyString())).willReturn("refresh");

        // when
        SignInResponse res = accountService.signIn(new SignInRequest("username", "password"));

        // then
        assertThat(res.getAccessToken()).isEqualTo("access");
        assertThat(res.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void signInExceptionByNoneMemberTest() {
        // given
        given(memberRepository.findByUsername(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> accountService.signIn(new SignInRequest("username", "password")))
                .isInstanceOf(LoginFailureException.class);
    }

    @Test
    void signInExceptionByInvalidPasswordTest() {
        // given
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> accountService.signIn(new SignInRequest("username", "password")))
                .isInstanceOf(LoginFailureException.class);
    }


    private SignUpRequest createSignUpRequest() {
        return new SignUpRequest("username", "password", "nickname");
    }

    private Member createMember() {
        return new Member( "username",  "password","nickname", emptyList());
    }

}