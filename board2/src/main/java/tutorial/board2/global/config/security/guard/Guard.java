package tutorial.board2.global.config.security.guard;
import tutorial.board2.domain.account.RoleType;

import java.util.List;

public abstract class Guard {
    // 인증된 사용자만 서비스 로직에 접근하므로 사용자 인증 처리 안함.
    // check 의 반환값에 따라 AccessDeniedException 발생하기에 throws 필요 없음.
    public final boolean check(Long id) {
        return hasRole(getRoleTypes()) || isResourceOwner(id);
    }

    abstract protected List<RoleType> getRoleTypes();
    abstract protected boolean isResourceOwner(Long id);

    private boolean hasRole(List<RoleType> roleTypes) {
        return AuthHelper.extractMemberRoles().containsAll(roleTypes);
    }
}
