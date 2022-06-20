package tutorial.board.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.Role;
import tutorial.board.domain.account.RoleType;
import tutorial.board.domain.account.dto.MemberInfoDto;
import tutorial.board.domain.account.dto.MemberSignUpDto;
import tutorial.board.domain.account.dto.MemberUpdateDto;
import tutorial.board.domain.account.repository.MemberRepository;
import tutorial.board.domain.account.repository.RoleRepository;
import tutorial.board.global.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {
        // 기본 권한 설정
        Role role = roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new Exception());

        Member member = memberSignUpDto.toEntity(List.of(role));
        member.encodePassword(passwordEncoder);

        if(memberRepository.findByUsername(member.getUsername()).isPresent()){
            throw new Exception("이미 존재하는 아이디입니다.");
        }
        memberRepository.save(member);
    }

    @Override
    public void update(MemberUpdateDto memberUpdateDto) throws Exception {
        Member member = getCurrentMember();

        memberUpdateDto.getNickname().ifPresent(member::updateNickname);
    }

    @Override
    public void updatePassword(String checkPassword, String toBePassword) throws Exception {
        Member member = getCurrentMember();

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        member.updatePassword(passwordEncoder, toBePassword);
    }

    @Override
    public void withdraw(String checkPassword) throws Exception {
        Member member = getCurrentMember();

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        memberRepository.delete(member);
    }

    @Override
    public MemberInfoDto getInfo(Long id) throws Exception {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        return new MemberInfoDto(member);
    }

    @Override
    public MemberInfoDto getMyInfo() throws Exception {
        return new MemberInfoDto(getCurrentMember());
    }

    private Member getCurrentMember() throws Exception{
        return memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
    }

}
