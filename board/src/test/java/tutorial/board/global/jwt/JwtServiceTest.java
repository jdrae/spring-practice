package tutorial.board.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired JwtService jwtService;
    @PersistenceContext EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private String username = "user";

    private void clear(){
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token){
        return JWT.require(HMAC512(secret)).build().verify(token);
    }

    @BeforeEach
    public void init(){
        Member member = Member.builder().username(username).password("1234").nickname("유저").roles(emptyList()).build();
        memberRepository.save(member);
        clear();
    }

    @Test
    void createAccessToken() {
        // given
        String accessToken = jwtService.createAccessToken(username);
        DecodedJWT verify = getVerify(accessToken);
        String subject = verify.getSubject();
        String foundUsername = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(foundUsername).isEqualTo(username);
    }

    @Test
    void createRefreshToken() {
        // given
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String foundUsername = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(foundUsername).isNull();
    }

    @Test
    void updateRefreshToken() throws InterruptedException {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        // then
        // 기존 토큰으로 찾을 수 없음
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        // 새로 발급한 토큰의 유저이름가 일치함
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getUsername()).isEqualTo(username);
    }

    @Test
    void destroyRefreshToken() {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        // when
        jwtService.destroyRefreshToken(username);
        clear();

        // then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        
        Member member = memberRepository.findByUsername(username).get();
        assertThat(member.getRefreshToken()).isNull();
    }
    
    @Test
    public void 토큰_유효성_검사() throws Exception{
        // given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        // then
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
    }

    @Test
    void sendAccessAndRefreshToken() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        // when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    void extractAccessToken() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequestWithToken(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }

    @Test
    void extractRefreshToken() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequestWithToken(accessToken, refreshToken);

        // when
        String extractRefreshToken = jwtService.extractRefreshToken(request)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        // then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    void extractUsername() throws Exception{
        // given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequestWithToken(accessToken, refreshToken);

        String extractAccessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        // when
        String extractUsername = jwtService.extractUsername(extractAccessToken)
                .orElseThrow(() -> new Exception("유저 이름이 없습니다."));

        // then
        assertThat(extractUsername).isEqualTo(username);
    }

    @Test
    void setAccessTokenHeader() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        // when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    void setRefreshTokenHeader() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        // when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }


    //== 도움 메소드 ==//
    private HttpServletRequest setRequestWithToken(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse,accessToken,refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }

}