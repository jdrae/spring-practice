package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    private Optional<String> nickname;
}
