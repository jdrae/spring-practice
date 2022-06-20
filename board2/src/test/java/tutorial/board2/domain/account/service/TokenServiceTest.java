package tutorial.board2.domain.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tutorial.board2.global.handler.JwtHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks // 의존성을 가지고 있는 객체들을 가짜로 만들어서 주입받을 수 있도록
    TokenService tokenService;
    @Mock // 객체들을 가짜로 만들어서 @InjectMocks로 지정된 객체에 주입
    JwtHandler jwtHandler;

    @BeforeEach
    void beforeEach() {
        // 단위테스트 수행시, 설정파일에서 읽어올 수 없기 때문에
        // 가짜 setter 의 역할로 필요한 값을 주입
        ReflectionTestUtils.setField(tokenService, "accessTokenMaxAgeSeconds", 10L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenMaxAgeSeconds", 10L);
        ReflectionTestUtils.setField(tokenService, "accessKey", "accessKey");
        ReflectionTestUtils.setField(tokenService, "refreshKey", "refreshKey");
    }

    @Test
    void createAccessTokenTest() {
        // given
        // 의존하는 가짜 객체의 행위가 반환해야할 데이터를 미리 준비하여 주입
        // given 안의 내용이 실행 되어 willReturn 값이 준비되어있음을 의미.
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("access");

        // when
        String token = tokenService.createAccessToken("subject");

        // then
        assertThat(token).isEqualTo("access");
        // 가짜 jwtHandler 객체가 createToken 을 수행했는지 검증.
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }

    @Test
    void createRefreshTokenTest() {
        // given
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("refresh");

        // when
        String token = tokenService.createRefreshToken("subject");

        // then
        assertThat(token).isEqualTo("refresh");
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }
}