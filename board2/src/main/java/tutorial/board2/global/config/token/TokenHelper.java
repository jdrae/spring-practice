package tutorial.board2.global.config.token;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tutorial.board2.global.handler.JwtHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

// TokenConfig 에서 직접 컨테이너에 빈 등록하기 때문에 @Component 없음
@RequiredArgsConstructor
public class TokenHelper {
    private final JwtHandler jwtHandler;
    private final String key;
    private final long maxAgeSeconds;

    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String ROLE_TYPES = "ROLE_TYPES";
    private static final String SEP = ",";

    public String createToken(PrivateClaims privateClaims){
        return jwtHandler.createToken(
                key,
                Map.of(MEMBER_ID, privateClaims.getMemberId(), ROLE_TYPES, privateClaims.getRoleTypes().stream().collect(joining(SEP))),
                maxAgeSeconds);
    }

    public Optional<PrivateClaims> parse(String token){ // 유효하지 않으면 비어있음
        return jwtHandler.parse(key, token).map(this::convert);
    }

    private PrivateClaims convert(Claims claims){
        return new PrivateClaims(
                claims.get(MEMBER_ID, String.class),
                Arrays.asList(claims.get(ROLE_TYPES, String.class).split(SEP))
        );
    }

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims{
        // 인증에 필요한 정보만 담음.
        private String memberId;
        private List<String> roleTypes;
    }
}
