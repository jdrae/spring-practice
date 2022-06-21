package tutorial.board2.global.handler;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
public class JwtHandler {

    private String BEARER = "Bearer ";

    public String createToken(String encodedKey, String subject, long maxAgeSeconds){
        Date now = new Date();
        return BEARER + Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L))
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();
    }

    public String extractSubject(String encodedKey, String token){
        return parse(encodedKey, token).getBody().getSubject();
    }

    public boolean validate(String encodedKey, String token){
        try{
            parse(encodedKey, token);
            return true;
        } catch(JwtException e){
            return false;
        }
    }

    private Jws<Claims> parse(String key, String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(untype(token));
    }

    private String untype(String token){
        return token.substring(BEARER.length()); // TODO: BEARER 없이 보내기
    }
}
