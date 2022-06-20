package tutorial.board.domain.account.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.Role;
import tutorial.board.domain.account.RoleType;
import tutorial.board.domain.account.dto.*;
import tutorial.board.domain.account.exception.MemberException;
import tutorial.board.domain.account.exception.MemberExceptionType;
import tutorial.board.domain.account.repository.MemberRepository;
import tutorial.board.domain.account.repository.RoleRepository;
import tutorial.board.global.exception.BaseException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired RoleRepository roleRepository;
    @PersistenceContext EntityManager em;

    String PASSWORD = "password";

    private void clear(){
        em.flush();
        em.clear();
    }

    private MemberSignUpDto getMemberSignUpDto() {
        return new MemberSignUpDto("user", PASSWORD, "nickname");
    }

    private MemberSignUpDto setMember() throws Exception {
        MemberSignUpDto memberSignUpDto = getMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        UserDetails user = User.builder().username(memberSignUpDto.getUsername()).password(memberSignUpDto.getPassword()).roles().build(); // roles 처리?

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, null));

        SecurityContextHolder.setContext(context);
        return memberSignUpDto;
    }

    @BeforeEach
    public void setDefaultRole(){
        Role defaultRole = new Role(RoleType.USER);
        roleRepository.save(defaultRole);
        clear();;
    }

    @AfterEach
    public void removeMember(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    @Test
    void signUp() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = getMemberSignUpDto();

        //when
        memberService.signUp(memberSignUpDto);
        clear();

        //then
        Member member = memberRepository.findByUsername(memberSignUpDto.getUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(memberSignUpDto.getUsername());
        assertThat(member.getNickname()).isEqualTo(memberSignUpDto.getNickname());
        assertThat(member.getRolesInString()[0]).isEqualTo("USER");
    }

    @Test
    void signup_아이디_중복() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = getMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        //then
        assertThat(
                assertThrows(BaseException.class,
                        () -> memberService.signUp(memberSignUpDto)).getExceptionType())
                .isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);
    }

    @Test
    void update_nickname() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        String updateName = "updated";
        memberService.update(new MemberUpdateDto(Optional.of(updateName)));

        // then
        memberRepository.findByUsername(memberSignUpDto.getUsername()).ifPresent(
                member -> {
                    assertThat(member.getNickname()).isEqualTo(updateName);
                }
        );
    }

    @Test
    void updatePassword() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        String updatePassword = "updated";
        memberService.updatePassword(PASSWORD, updatePassword);
        clear();

        // then
        Member member = memberRepository.findByUsername(memberSignUpDto.getUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.matchPassword(passwordEncoder, updatePassword)).isTrue();
    }

    @Test
    void updatePassword_password_not_matched() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();
        String updatePassword = "updated";

        // then
        assertThat(
                assertThrows(BaseException.class,
                        ()-> memberService.updatePassword(PASSWORD + "1", updatePassword))
                        .getExceptionType())
                .isEqualTo(MemberExceptionType.WRONG_PASSWORD);
    }

    @Test
    void withdraw() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        // when
        memberService.withdraw(PASSWORD);

        //then
        assertThat(
                assertThrows(BaseException.class,
                        ()-> memberRepository.findByUsername(memberSignUpDto.getUsername())
                                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)))
                        .getExceptionType())
                .isEqualTo(MemberExceptionType.NOT_FOUND_MEMBER);
    }

    @Test
    void withdraw_password_not_matched() throws Exception {
        // given
        MemberSignUpDto memberSignUpDto = setMember();

        //then
        assertThat(
                assertThrows(BaseException.class,
                        ()-> memberService.withdraw(PASSWORD + "1"))
                        .getExceptionType())
                .isEqualTo(MemberExceptionType.WRONG_PASSWORD);
    }
    @Test
    void getInfo() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();
        Member member = memberRepository.findByUsername(memberSignUpDto.getUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        clear();

        //when
        MemberInfoDto info = memberService.getInfo(member.getId());

        //then
        assertThat(info.getUsername()).isEqualTo(memberSignUpDto.getUsername());
        assertThat(info.getNickname()).isEqualTo(memberSignUpDto.getNickname());
    }

    @Test
    void getMyInfo() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        MemberInfoDto info = memberService.getMyInfo();

        //then
        assertThat(info.getUsername()).isEqualTo(memberSignUpDto.getUsername());
        assertThat(info.getNickname()).isEqualTo(memberSignUpDto.getNickname());
    }


}