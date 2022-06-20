package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithdrawDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String checkPassword;
}
