package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpDto {

    @NotBlank @Length(min = 3, max = 20)
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    @NotBlank @Length(min = 2, max = 20)
    private String nickname;

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
