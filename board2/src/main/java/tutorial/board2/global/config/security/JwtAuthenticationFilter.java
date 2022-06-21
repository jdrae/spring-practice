package tutorial.board2.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import tutorial.board2.global.config.token.TokenHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// @Component 하면 자동으로 필터 체인에 등록되기 때문에 중복 등록.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenHelper accessTokenHelper;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // type 은 AuthHelper 에서 isAccessTokenType 할때 검증하는 값.
        String token = extractToken(request); // 요청의 헤더값꺼냄
        if(validateToken(token)) { // 토큰 유효 검증
            setAuthentication(token); // security 컨텍스트에 사용자 등록
        }
        chain.doFilter(request, response);
    }

    private String extractToken(ServletRequest request) {
        return ((HttpServletRequest)request).getHeader("Authorization");
    }

    // access 토큰으로만 요청을 할 수 있고
    // 토큰 재발급 api 를 permitAll 로 설정했기 때문에
    // 리프레시 토큰으로 요청한 사용자를 구분할 필요가 없음.
    private boolean validateToken(String token) {
        return token != null && accessTokenHelper.validate(token);
    }

    private void setAuthentication(String token) {
        String userId = accessTokenHelper.extractSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }


}
