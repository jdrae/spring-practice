package tutorial.board2.factory.entity;

import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.Role;

import java.util.List;

import static java.util.Collections.emptyList;

public class MemberFactory {

    public static Member createMember() {
        return new Member("username",  "123456a!", "nickname", emptyList());
    }

    public static Member createMember(String password, String username, String nickname) {
        return new Member(username, password, nickname, emptyList());
    }

    public static Member createMemberWithRoles(List<Role> roles) {
        return new Member("username", "123456a!",  "nickname", roles);
    }
}
