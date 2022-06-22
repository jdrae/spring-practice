package tutorial.board2.domain.account.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "로그인 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @NotBlank(message = "{signInRequest.username.notBlank}")
    private String username;

    @NotBlank(message = "{signInRequest.password.notBlank}")
    private String password;
}
