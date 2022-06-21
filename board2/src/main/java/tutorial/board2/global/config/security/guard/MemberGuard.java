package tutorial.board2.global.config.security.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorial.board2.domain.account.RoleType;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MemberGuard {

    private final AuthHelper authHelper;

    public boolean check(Long id){
        return authHelper.isAuthenticated()         // 인증되었는지
                && authHelper.isAccessTokenType()   // access 토큰이 있는지
                && hasAuthority(id);                // 자원 접근 권한이 있는지
    }

    private boolean hasAuthority(Long id) {
        Long memberId = authHelper.extractMemberId();
        Set<RoleType> memberRoles = authHelper.extractMemberRoles();
        return id.equals(memberId) || memberRoles.contains(RoleType.ROLE_ADMIN);
    }


}
