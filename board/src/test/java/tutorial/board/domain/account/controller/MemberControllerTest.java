package tutorial.board.domain.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.Role;
import tutorial.board.domain.account.RoleType;
import tutorial.board.domain.account.dto.MemberSignUpDto;
import tutorial.board.domain.account.dto.UpdatePasswordDto;
import tutorial.board.domain.account.repository.MemberRepository;
import tutorial.board.domain.account.repository.RoleRepository;
import tutorial.board.domain.account.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @PersistenceContext EntityManager em;

    ObjectMapper objectMapper = new ObjectMapper();

    private static String SIGN_UP_URL = "/signup";

    private String username = "user";
    private String password = "password1234@";
    private String nickname = "nick";

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    //== helper method ==//
    private void clear(){
        em.flush();
        em.clear();
    }

    private String getSignUpData(String username, String password, String nickname) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new MemberSignUpDto(username, password, nickname));
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);

        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @BeforeEach
    public void setDefaultRole(){
        roleRepository.save(new Role(RoleType.USER));
        clear();;
    }


    //== test ==/
    @Test
    void signup_success() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);

        //when
        signUp(signUpData);

        //then
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new Exception());
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void signup_no_username() throws Exception {
        //given
        String noUsername = getSignUpData("", password, nickname);
        String noPassword = getSignUpData(username, "", nickname);
        String noNickname = getSignUpData(username, password, "");

        //when
        signUp(noUsername); // 예외 있어도 200
        signUp(noPassword);
        signUp(noNickname);
        
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void updateBasicInfo() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);
        
        String accessToken = getAccessToken();
        Map<String, String> map = new HashMap<>();
        map.put("nickname",nickname + "123");
        String updateData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                put("/member")
                        .header(accessHeader, BEARER + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new Exception());
        assertThat(member.getNickname()).isEqualTo(nickname + "123");
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void updatePassword() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(password, password + "123");
        String updatePassword = objectMapper.writeValueAsString(updatePasswordDto);

        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"123", member.getPassword())).isTrue();
    }

    @Test
    void updatePassword_wrong_checkPassword() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(password + "1", password + "123");
        String updatePassword = objectMapper.writeValueAsString(updatePasswordDto);

        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"123", member.getPassword())).isFalse();
    }


    @Test
    public void 비밀번호수정_실패_바꾸려는_비밀번호_형식_올바르지않음() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);
        map.put("toBePassword","123123");

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/member/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123123", member.getPassword())).isFalse();
    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);

        String withdrawData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(withdrawData))
                .andExpect(status().isOk());

        //then
        assertThrows(Exception.class, () -> memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다")));
    }

    @Test
    public void 회원탈퇴_실패_비밀번호틀림() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password+11);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member).isNotNull();
    }

    @Test
    public void 회원탈퇴_실패_권한이없음() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword",password);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/member")
                                .header(accessHeader,BEARER+accessToken+"1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isForbidden());

        //then
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member).isNotNull();
    }

    @Test
    public void 내정보조회_성공() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        //when
        MvcResult result = mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();

        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getNickname()).isEqualTo(map.get("nickname"));

    }



    @Test
    public void 내정보조회_실패_JWT없음() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        //when,then
        mockMvc.perform(
                        get("/member")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());
    }


    /**
     * 회원정보조회 성공
     * 회원정보조회 실패 -> 회원이없음
     * 회원정보조회 실패 -> 권한이없음
     */
    @Test
    public void 회원정보조회_성공() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = memberRepository.findAll().get(0).getId();

        //when

        MvcResult result = mockMvc.perform(
                        get("/member/"+id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member.getUsername()).isEqualTo(map.get("username"));
        assertThat(member.getNickname()).isEqualTo(map.get("nickname"));
    }



    @Test
    public void 회원정보조회_실패_없는회원조회() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();


        //when
        MvcResult result = mockMvc.perform(
                        get("/member/2211")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound()).andReturn();
    }



    @Test
    public void 회원정보조회_실패_JWT없음() throws Exception {
        //given
        String signUpData = getSignUpData(username, password, nickname);
        signUp(signUpData);

        String accessToken = getAccessToken();

        //when,then
        mockMvc.perform(
                        get("/member/1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());

    }
}