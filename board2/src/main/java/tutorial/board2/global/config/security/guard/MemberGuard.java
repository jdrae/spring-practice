package tutorial.board2.global.config.security.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorial.board2.domain.account.RoleType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberGuard extends Guard{

    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return id.equals(AuthHelper.extractMemberId());
    }

}
