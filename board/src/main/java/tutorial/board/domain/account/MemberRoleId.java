package tutorial.board.domain.account;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberRoleId implements Serializable {
    /*
    * composite key에서는 key들의 순서가 중요합니다.
    * 인덱스 구조가 첫번째 필드로 정렬된 뒤에, 그 다음으로 두번째 필드로 정렬되기 때문에,
    * 만약 중복도가 높은 필드가 첫번째로 생성된다면, 필터링되는 레코드가 적어서 인덱스의 효과를 보지 못하게 됩니다.
    * composite key의 순서를 제어하려면, schema를 구성하는 스크립트를 직접 작성하거나 알파벳 순으로 필드의 이름을 변경하는 방법이 있겠습니다.
    * */
    private Member member;
    private Role role;
}
