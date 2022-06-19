package tutorial.board.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class MemberUpdateDto {

    private final Optional<String> nickname;
}
