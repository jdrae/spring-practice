package tutorial.board2.global.config.token;

import lombok.RequiredArgsConstructor;
import tutorial.board2.global.handler.JwtHandler;

// TokenConfig 에서 직접 컨테이너에 빈 등록하기 때문에 @Component 없음
@RequiredArgsConstructor
public class TokenHelper {
    private final JwtHandler jwtHandler;
    private final String key;
    private final long maxAgeSeconds;

    public String createToken(String subject){
        return jwtHandler.createToken(key, subject, maxAgeSeconds);
    }

    public boolean validate(String token){
        return jwtHandler.validate(key, token);
    }

    public String extractSubject(String token){
        return jwtHandler.extractSubject(key, token);
    }
}
