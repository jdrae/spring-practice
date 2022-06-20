package tutorial.board2.global.handler;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtHandlerTest {
    JwtHandler jwtHandler = new JwtHandler();

    @Test
    void createTokenTest() {
        // given, when
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 60L);

        // then
        assertThat(token).contains("Bearer ");
    }

    @Test
    void extractSubjectTest() {
        // given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String subject = "subject";
        String token = createToken(encodedKey, subject, 60L);

        // when
        String extractedSubject = jwtHandler.extractSubject(encodedKey, token);

        // then
        assertThat(extractedSubject).isEqualTo(subject);
    }

    @Test
    void validateTest() {
        // given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 60L);

        // when
        boolean isValid = jwtHandler.validate(encodedKey, token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void invalidateByInvalidKeyTest() {
        // 지정된 key 가 아닌 다른 걸로 validate 하면 안됨
        // given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 60L);

        // when
        boolean isValid = jwtHandler.validate("invalid", token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void invalidateByExpiredTokenTest() {
        // 유효시간 0 초로 하면 생성과 동시에 만료.
        // given
        String encodedKey = Base64.getEncoder().encodeToString("myKey".getBytes());
        String token = createToken(encodedKey, "subject", 0L);

        // when
        boolean isValid = jwtHandler.validate(encodedKey, token);

        // then
        assertThat(isValid).isFalse();
    }

    private String createToken(String encodedKey, String subject, long maxAgeSeconds) {
        return jwtHandler.createToken(
                encodedKey,
                subject,
                maxAgeSeconds);
    }
}