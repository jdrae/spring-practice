package tutorial.board2.domain.account.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignInResponse;
import tutorial.board2.domain.account.exception.MemberNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;
import tutorial.board2.domain.account.service.AccountService;
import tutorial.board2.init.TestInitDB;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc // 기본 설정은 WebEnvironment.MOCK 이라 톰캣서버가 아님. 하지만 MockMvc 는 자동 주입이 되지 않음.
@ActiveProfiles(value = "test") // "local" 일때 자동으로 InitDB 가 데이터베이스를 초기화 하므로 다른 profile 지정.
public class MemberControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired MockMvc mockMvc;

    @Autowired TestInitDB initDB;
    @Autowired AccountService accountService;
    @Autowired MemberRepository memberRepository;


    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
    }

    @Test
    void readTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        get("/api/members/{id}", member.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);
        SignInResponse signInRes = accountService.signIn(new SignInRequest(initDB.getMember1Username(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/members/{id}", member.getId())
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);
        SignInResponse adminSignInRes = accountService.signIn(new SignInRequest(initDB.getAdminUsername(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/members/{id}", member.getId())
                                .header("Authorization", adminSignInRes.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUnauthorizedByNoneTokenTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        delete("/api/members/{id}", member.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/entry-point"));
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);
        SignInResponse attackerSignInRes = accountService.signIn(new SignInRequest(initDB.getMember2Username(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/members/{id}", member.getId()).header("Authorization", attackerSignInRes.getAccessToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

    @Test
    void deleteAccessDeniedByRefreshTokenTest() throws Exception {
        // given
        Member member = memberRepository.findByUsername(initDB.getMember1Username()).orElseThrow(MemberNotFoundException::new);
        SignInResponse signInRes = accountService.signIn(new SignInRequest(initDB.getMember1Username(), initDB.getPassword()));

        // when, then
        mockMvc.perform(
                        delete("/api/members/{id}", member.getId()).header("Authorization", signInRes.getRefreshToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exception/access-denied"));
    }

}
