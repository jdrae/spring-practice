package tutorial.board2.global.config.security.guard;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tutorial.board2.domain.account.RoleType;
import tutorial.board2.global.config.security.CustomAuthenticationToken;
import tutorial.board2.global.config.security.CustomUserDetails;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthHelper {

    public boolean isAuthenticated(){
        return getAuthentication() instanceof CustomAuthenticationToken &&
                getAuthentication().isAuthenticated();
    }

    public Long extractMemberId(){
        return Long.valueOf(getUserDetails().getUserId());
    }

    public Set<RoleType> extractMemberRoles(){
        return getUserDetails().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleType::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean isAccessTokenType(){
        // 인증되지 않은 사용자도 AnonymousAuthenticationToken 을 발급받기 때문에
        // 직접 지정한 CustomAuthenticationToken 의 인스턴스만 인증된 것으로 판별.
        return "access".equals(((CustomAuthenticationToken) getAuthentication()).getType());
    }

    public boolean isRefreshTokenType(){
        return "refresh".equals(((CustomAuthenticationToken) getAuthentication()).getType());
    }

    //== helper ==//
    private CustomUserDetails getUserDetails(){
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }

    private Authentication getAuthentication(){
        // 현재 콘텍스트에서 인증된 유저 정보 가져옴
        // ThreadLocal 을 이용하여 관리하는데, 같은 스래드 내에서는 데이터를 공유할 수 있음.
        // JwtAuthenticationFilter 에서 ThreadLocal 에 사용자 정보를 저장하게 됨.
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
