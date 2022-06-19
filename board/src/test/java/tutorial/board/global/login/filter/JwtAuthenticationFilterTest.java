package tutorial.board.global.login.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.repository.MemberRepository;
import tutorial.board.global.jwt.JwtService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtService jwtService;
    @PersistenceContext EntityManager em;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "user";
    private static String PASSWORD = "1234";

    private static String LOGIN_URL = "/login";
    private static String TEST_URL = "/login" + "123"; // 임의의 테스트 주소


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";


    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .password(passwordEncoder.encode(PASSWORD))
                .nickname("유저")
                .roles(emptyList())
                .build());
        clear();
    }

    /*
    * AccessToken: 없음
    * RefreshToken: 없음
    * 403 Forbidden
    * */
    @Test
    void Access_Refresh_모두_없음() throws Exception {
        mockMvc.perform(get(TEST_URL))
                .andExpect(status().isForbidden());
    }

    /*
     * AccessToken: 존재, 유효
     * RefreshToken: 없음
     * 404 NotFound
     * */
    @Test
    void AccessToken_인증() throws Exception {
        //given
        String accessToken = getAccessToken();

        //then
        mockMvc.perform(get(TEST_URL).header(accessHeader, accessToken))
                .andExpectAll(status().isNotFound()); // 없는 주소로 보냈기 때문에 NotFound

    }

    /*
     * AccessToken: 존재, 무효
     * RefreshToken: 없음
     * 403 Forbidden
     * */
    @Test
    void 무효한_AccessToken_인증_실패() throws Exception {
        //given
        String accessToken = getAccessToken();

        //then
        mockMvc.perform(get(TEST_URL).header(accessHeader, accessToken + "1"))
                .andExpectAll(status().isForbidden());
    }

    /*
     * AccessToken: 없음
     * RefreshToken: 존재, 유효
     * 200 ok
     * */
    @Test
    void RefreshToken_재발급() throws Exception {
        //given
        String refreshToken = getRefreshToken();

        //when
        MvcResult result = mockMvc.perform(get(TEST_URL).header(refreshHeader, refreshToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String accessToken = result.getResponse().getHeader(accessHeader);
        String subject = JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(accessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    /*
     * AccessToken: 없음
     * RefreshToken: 존재, 무효
     * 403 Forbidden
     * */
    @Test
    void 무효한_RefreshToken_재발급_실패() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String noBearer = (String) accessAndRefreshToken.get(refreshHeader);

        //when
        String wrongToken = BEARER + noBearer + "1";

        //then
        mockMvc.perform(get(TEST_URL).header(refreshHeader, noBearer))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(TEST_URL).header(refreshHeader, wrongToken))
                .andExpect(status().isForbidden());
    }

    /*
     * AccessToken: 유효
     * RefreshToken: 유효
     * 200
     * */
    @Test
    void Access유효_Refresh유효_Refresh만_재발급_안함() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when
        MvcResult result = mockMvc.perform(get(TEST_URL)
                .header(accessHeader, BEARER + accessToken)
                .header(refreshHeader, BEARER + refreshToken))
                .andExpect(status().isOk()) // 없는 URL 이지만 ok?
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(responseAccessToken).getSubject();

        //then
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT); // accessToken 은 재발급 됨
        assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
    }

    /*
     * AccessToken: 무효
     * RefreshToken: 유효
     * 200
     * */
    @Test
    void Access무효_Refresh유효_Refresh만_재발급_안함() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when
        MvcResult result = mockMvc.perform(get(TEST_URL)
                        .header(accessHeader, BEARER + accessToken + "1") // 무효
                        .header(refreshHeader, BEARER + refreshToken))
                .andExpect(status().isOk()) // access 무효하지만 OK
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(responseAccessToken).getSubject();

        //then
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT); // accessToken 은 재발급 됨
        assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
    }

    /*
     * AccessToken: 유효
     * RefreshToken: 무효
     * 200 ok 또는 404 NotFound : 주소 존재 여부에 따라
     * */
    @Test
    void Access유효_Refresh무효_둘다_재발급_안함() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when
        MvcResult result = mockMvc.perform(get(TEST_URL)
                        .header(accessHeader, BEARER + accessToken)
                        .header(refreshToken, BEARER + refreshToken + "1")) // 무효
                .andExpect(status().isNotFound()) // 인증은 되지만 없는 주소 NotFound
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        //then
        assertThat(responseAccessToken).isNull(); // accessToken은 재발급되지 않음
        assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
    }

    /*
     * AccessToken: 무효
     * RefreshToken: 무효
     * 403 Forbidden
     * */
    @Test
    void Access무효_Refresh무효_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when
        MvcResult result = mockMvc.perform(get(TEST_URL)
                        .header(accessHeader, BEARER + accessToken + "1") // 무효
                        .header(refreshToken, BEARER + refreshToken + "1")) // 무효
                .andExpect(status().isForbidden())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        //then
        assertThat(responseAccessToken).isNull(); // accessToken은 재발급되지 않음
        assertThat(responseRefreshToken).isNull(); //refreshToken은 재발급되지 않음
    }

    @Test
    void 로그인_주소로_보내면_필터작동_안함() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //then
        mockMvc.perform(get(LOGIN_URL)  //get인 경우 config에서 permitAll을 했기에 notFound
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound()) // ?
                .andReturn();

        mockMvc.perform(post(LOGIN_URL)  //post 의 경우 /login 은 필터 통과 안되기 때문에 로그인 실패
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isUnauthorized()) // 로그인 실패
                .andReturn();
    }

    private void clear(){
        em.flush();
        em.clear();
    }

    private Map<String, String> getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private Map<String, String> getAccessAndRefreshToken() throws Exception {
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);
        MvcResult result = mockMvc.perform(
                                post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader, accessToken);
        tokenMap.put(refreshHeader, refreshToken);

        return tokenMap;
    }

    private String getAccessToken() throws Exception {
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        return BEARER + accessAndRefreshToken.get(accessHeader);
    }

    private String getRefreshToken() throws Exception {
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        return BEARER + accessAndRefreshToken.get(refreshHeader);
    }
}