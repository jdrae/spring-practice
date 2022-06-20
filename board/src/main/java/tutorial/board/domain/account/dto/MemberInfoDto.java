package tutorial.board.domain.account.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tutorial.board.domain.account.Member;

@Data //getter & setter
@NoArgsConstructor
public class MemberInfoDto {

    private String username;
    private String nickname;

    @Builder
    public MemberInfoDto(Member member){
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }
}
