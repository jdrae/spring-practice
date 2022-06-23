package tutorial.board2.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.dto.MemberDto;
import tutorial.board2.domain.account.exception.MemberNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDto read(Long id) {
        return memberToMemberDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }

    // guard 와 service 에서 두번 쿼리에 접근하게 되기에 @PreAuthorize 설정
    // false 반환: AccessDeniedException -> ExceptionAdvice 에서 캐치
    @Transactional
    @PreAuthorize("@memberGuard.check(#id)")
    public void delete(Long id){
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member); // deleteById 는 SELECT + DELETE 이므로 위의 findById 의 SELECT 과 겹침
    }

    public MemberDto memberToMemberDto(Member member){
        return new MemberDto(member.getId(), member.getUsername(), member.getNickname());
    }
}
