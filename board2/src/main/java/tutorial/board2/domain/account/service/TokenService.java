package tutorial.board2.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tutorial.board2.global.handler.JwtHandler;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtHandler jwtHandler;

    @Value("${jwt.max-age.access}")
    private long accessTokenMaxAgeSeconds;

    @Value("${jwt.max-age.refresh}")
    private long refreshTokenMaxAgeSeconds;

    @Value("${jwt.key.access}")
    private String accessKey;

    @Value("${jwt.key.refresh}")
    private String refreshKey;

    public String createAccessToken(String subject) {
        return jwtHandler.createToken(accessKey, subject, accessTokenMaxAgeSeconds);
    }

    public String createRefreshToken(String subject) {
        return jwtHandler.createToken(refreshKey, subject, refreshTokenMaxAgeSeconds);
    }
}
