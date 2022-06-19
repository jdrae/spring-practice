package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.Role;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberSignUpDto {

    private final String username;
    private final String password;
    private final String nickname;

    public Member toEntity(List<Role> roles){
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .roles(roles)
                .build();
    }

    @Override
    public String toString() {
        return username + ":" + nickname;
    }
}
