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

    public static boolean isAuthenticated(){
        return getAuthentication() instanceof CustomAuthenticationToken &&
                getAuthentication().isAuthenticated();
    }

    public static Long extractMemberId(){
        return Long.valueOf(getUserDetails().getUserId());
    }

    public static Set<RoleType> extractMemberRoles(){
        return getUserDetails().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleType::valueOf)
                .collect(Collectors.toSet());
    }

    //== helper ==//
    private static CustomUserDetails getUserDetails(){
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }

    private static Authentication getAuthentication(){
        // 현재 콘텍스트에서 인증된 유저 정보 가져옴
        // ThreadLocal 을 이용하여 관리하는데, 같은 스래드 내에서는 데이터를 공유할 수 있음.
        // JwtAuthenticationFilter 에서 ThreadLocal 에 사용자 정보를 저장하게 됨.
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
