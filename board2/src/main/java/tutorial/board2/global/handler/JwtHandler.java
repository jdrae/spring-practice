package tutorial.board2.global.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtHandler {

    private String type = "Bearer ";

    public String createToken(String key, Map<String, Object> privateClaims, long maxAgeSeconds){
        Date now = new Date();
        return type + Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L))
                .addClaims(privateClaims)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    public Optional<Claims> parse(String key, String token){
        try{
            return Optional.of(Jwts.parser()
                    .setSigningKey(key.getBytes())
                    .parseClaimsJws(untype(token))
                    .getBody()
            );

        } catch(JwtException e){
            return Optional.empty();
        }
    }

    private String untype(String token){
        return token.substring(type.length()); // TODO: BEARER 없이 보내기
    }
}
