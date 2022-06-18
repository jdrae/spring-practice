package tutorial.board.domain.account.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.MemberRole;
import tutorial.board.domain.account.Role;
import tutorial.board.domain.account.RoleType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired RoleRepository roleRepository;
    @PersistenceContext EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }

    @AfterEach
    private void after(){
        em.clear();
    }

    @Test
    void 회원저장() throws Exception {
        //given
        Member member = createMember("user", "1234", "유저");

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
                .orElseThrow( () -> new RuntimeException("저장된 회원이 없습니다."));

        assertThat(foundMember).isSameAs(savedMember);
        assertThat(foundMember).isSameAs(member);
    }

    @Test
    void 오류_회원가입시_아이디가_없음() throws Exception {
        //given
        Member member = createMember(null, "1234", "유저");

        //when

        //then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }

    @Test
    void 오류_회원가입시_중복된_아이디가_있음() throws Exception {
        //given
        Member member1 = createMember("user", "1234", "유저1");
        Member member2 = createMember("user", "1234", "유저2");

        memberRepository.save(member1);
        clear();

        //when

        //then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.save(member2));
    }

    @Test
    void 회원수정() throws Exception {
        //given
        Member member = createMember("user", "1234", "유저");

        Member savedMember = memberRepository.save(member);
        clear();

        String updatePassword = "updatePassword";
        String updateNickname = "updateNickname";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new Exception());

        foundMember.updatePassword(passwordEncoder, updatePassword);
        foundMember.updateNickname(updateNickname);
        em.flush();

        //then
        Member updatedMember = memberRepository.findById(foundMember.getId())
                .orElseThrow(() -> new Exception());

        assertThat(updatedMember).isSameAs(foundMember);

        assertThat(updatedMember.getNickname()).isEqualTo(updateNickname);
        assertThat(updatedMember.getNickname()).isNotEqualTo(member.getNickname());

        assertThat(passwordEncoder.matches(updatePassword, updatedMember.getPassword())).isTrue();
    }

    @Test
    void 회원삭제() throws Exception {
        //given
        Member member = createMember("user", "1234", "유저");

        memberRepository.save(member);
        clear();

        //when
        memberRepository.delete(member);
        clear();

        //then
        assertThrows(Exception.class,
                () -> memberRepository.findById(member.getId())
                        .orElseThrow( () -> new Exception()));
    }

    @Test
    void existByUsername() throws Exception {
        //given
        String username = "user";
        Member member = createMember(username, "1234", "유저");
        memberRepository.save(member);
        clear();

        //when

        //then
        assertThat(memberRepository.existsByUsername(username)).isTrue();
        assertThat(memberRepository.existsByUsername(username+"123")).isFalse();
    }

    @Test
    void findByUsername() throws Exception {
        //given
        String username = "user";
        Member member = createMember(username, "1234", "유저");
        memberRepository.save(member);
        clear();

        //when
        Member foundMember = memberRepository.findByUsername(username).get();

        //then
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember.getNickname()).isEqualTo(member.getNickname());
        assertThrows(Exception.class,
                () -> memberRepository.findByUsername(username+"123")
                        .orElseThrow(() -> new Exception()));
    }

    @Test
    void 회원가입시_생성시간_등록() throws Exception {
        //given
        Member member = createMember("user", "1234", "유저");
        memberRepository.save(member);
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new Exception());

        //then
        assertThat(foundMember.getCreatedDate()).isNotNull();
        assertThat(foundMember.getLastModifiedDate()).isNotNull();
        assertThat(foundMember.getCreatedDate()).isEqualTo(foundMember.getLastModifiedDate());
    }

    @Test
    void 회원역할_일괄_저장() throws Exception {
        // Member 를 저장하면 MemberRole 도 데이터베이스에 저장됨

        //given
        List<Role> roles = createRoles();
        roleRepository.saveAll(roles);
        clear();

        Member member = createMemberWithRoles(roleRepository.findAll());
        memberRepository.save(member);
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new Exception());
        Set<MemberRole> memberRoles = foundMember.getRoles();

        //then
        assertThat(memberRoles.size()).isEqualTo(roles.size());
    }

    @Test
    void 회원역할_일괄_삭제() throws Exception {
        // Member 를 제거하면 MemberRole 도 함께 제거됨

        //given
        List<Role> roles = createRoles();
        roleRepository.saveAll(roles);
        clear();

        Member member = createMemberWithRoles(roleRepository.findAll());
        memberRepository.save(member);
        clear();

        //when
        memberRepository.deleteById(member.getId());
        clear();

        //then
        List<MemberRole> result = em.createQuery("select mr from MemberRole mr", MemberRole.class).getResultList();
        assertThat(result.size()).isZero();
    }

    private List<Role> createRoles(){
        List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN, RoleType.ROLE_USER);
        List<Role> roles = roleTypes.stream()
                .map(roleType -> new Role(roleType)).collect(Collectors.toList());
        return roles;
    }

    private Member createMemberWithRoles(List<Role> roles) {
        Member member = Member.builder()
                .username("user")
                .password("1234")
                .nickname("유저")
                .roles(roles)
                .build();
        return member;
    }

    private Member createMember(String username, String password, String nickname) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .roles(emptyList())
                .build();
        return member;
    }

}