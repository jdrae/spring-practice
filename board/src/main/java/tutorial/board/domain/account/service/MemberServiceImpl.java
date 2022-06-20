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
import tutorial.board.domain.account.exception.*;
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
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }
        memberRepository.save(member);
    }

    @Override
    public void update(MemberUpdateDto memberUpdateDto) throws MemberException {
        Member member = getCurrentMember();

        memberUpdateDto.getNickname().ifPresent(member::updateNickname);
    }

    @Override
    public void updatePassword(String checkPassword, String toBePassword) throws MemberException {
        Member member = getCurrentMember();

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw  new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        member.updatePassword(passwordEncoder, toBePassword);
    }

    @Override
    public void withdraw(String checkPassword) throws MemberException {
        Member member = getCurrentMember();

        if(!member.matchPassword(passwordEncoder, checkPassword)){
            throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
        }

        memberRepository.delete(member);
    }

    @Override
    public MemberInfoDto getInfo(Long id) throws MemberException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        return new MemberInfoDto(member);
    }

    @Override
    public MemberInfoDto getMyInfo() throws MemberException {
        return new MemberInfoDto(getCurrentMember());
    }

    private Member getCurrentMember() throws MemberException{
        return memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

}
