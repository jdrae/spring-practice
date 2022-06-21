package tutorial.board2.factory.entity;

import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;

public class RoleFactory {

    public static Role createRole() {
        return new Role(RoleType.ROLE_NORMAL);
    }
}
