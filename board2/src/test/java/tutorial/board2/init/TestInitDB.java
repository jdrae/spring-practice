package tutorial.board2.init;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;
import tutorial.board2.domain.account.exception.RoleNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;
import tutorial.board2.domain.account.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestInitDB {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private String adminUsername = "admin";
    private String member1Username = "member1";
    private String member2Username = "member2";
    private String password = "123456a!";

    @Transactional
    public void initDB() {
        initRole();
        initTestAdmin();
        initTestMember();
    }

    private void initRole() {
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(Role::new).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        memberRepository.save(
                new Member(adminUsername, passwordEncoder.encode(password), "admin",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initTestMember() {
        memberRepository.saveAll(
                List.of(
                        new Member(member1Username, passwordEncoder.encode(password), "member1",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))),
                        new Member(member2Username, passwordEncoder.encode(password), "member2",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))))
        );
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getMember1Username() {
        return member1Username;
    }

    public String getMember2Username() {
        return member2Username;
    }

    public String getPassword() {
        return password;
    }
}