package tutorial.board.domain.account.dto;

import lombok.Builder;
import lombok.Data;
import tutorial.board.domain.account.Member;

@Data //getter & setter
public class MemberInfoDto {

    private final String username;
    private final String nickname;

    @Builder
    public MemberInfoDto(Member member){
        this.username = member.getUsername();
        this.nickname = member.getNickname();
    }
}
