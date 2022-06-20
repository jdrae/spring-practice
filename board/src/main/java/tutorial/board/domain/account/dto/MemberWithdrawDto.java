package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class MemberWithdrawDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String checkPassword;
}
