package tutorial.board2.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import tutorial.board2.domain.account.service.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// @Component 하면 자동으로 필터 체인에 등록되기 때문에 중복 등록.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // type 은 AuthHelper 에서 isAccessTokenType 할때 검증하는 값.
        String token = extractToken(request); // 요청의 헤더값꺼냄
        if(validateAccessToken(token)) { // 토큰 유효 검증
            setAccessAuthentication("access", token); // security 컨텍스트에 사용자 등록
        } else if(validateRefreshToken(token)) {
            setRefreshAuthentication("refresh", token);
        }
        chain.doFilter(request, response);
    }

    private String extractToken(ServletRequest request) {
        return ((HttpServletRequest)request).getHeader("Authorization");
    }

    private boolean validateAccessToken(String token) {
        return token != null && tokenService.validateAccessToken(token);
    }

    private boolean validateRefreshToken(String token) {
        return token != null && tokenService.validateRefreshToken(token);
    }

    private void setAccessAuthentication(String type, String token) {
        String userId = tokenService.extractAccessTokenSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(type, userDetails, userDetails.getAuthorities()));
    }

    private void setRefreshAuthentication(String type, String token) {
        String userId = tokenService.extractRefreshTokenSubject(token);
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        // SecurityContextHolder에 있는 ContextHolder에다가 Authentication 인터페이스의 구현체 CustomAuthenticationToken를 등록
        SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(type, userDetails, userDetails.getAuthorities()));
    }

}
