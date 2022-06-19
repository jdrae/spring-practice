package tutorial.board.global.login.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.repository.MemberRepository;
import tutorial.board.global.jwt.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    // OncePerRequestFilter: 모든 서블릿 컨테이너에서 요청 디스패치당 단일 실행을 보장하는 것을 목표로 하는 필터 기본 클래스

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/login";

    /**
     * 1. 리프레시 토큰 있을때: 유효하다면 AccessToken 재발급후, 필터는 진행하지 않는다.
     * 2. 리프레시 토큰 없을때: 유저정보 저장후 필터 계속 진행
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)){
            log.info("로그인주소 체크 안함");
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if(refreshToken != null){
            log.info("refreshToken 유효함");
            // refreshToken 이 유효할때
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            // refreshToken 이 있으면 인증은 처리하지 않음
            return;
        }
        log.info("refreshToken 무효함");
        checkAccessTokenAndAuthentication(request, response, filterChain);

    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken){
        log.info("refresh 토큰 검증후 access 토큰 재발행");
        // refresh 토큰 검증후 access 토큰 재발행
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getUsername())));
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        log.info("access 토큰 검증 후 인증 진행");

        // access 토큰 검증 후 인증 진행
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(
                        accessToken -> jwtService.extractUsername(accessToken).ifPresent(
                                        username -> memberRepository.findByUsername(username).ifPresent(
                                                member -> saveAuthentication(member))
                                )
                );

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member member){
        log.info("access 토큰 검증완료. 유저 생성.");

        UserDetails user = User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRolesInString())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authoritiesMapper.mapAuthorities(user.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
