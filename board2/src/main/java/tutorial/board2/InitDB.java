package tutorial.board2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
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

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local") // application.yml 에서 spring.profiles.active: local 설정
public class InitDB {
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

//    @PostConstruct // 빈의 생성과 의존성 주입이 끝난 뒤 수행할 초기화 코드. aop 적용 안됨.
    @EventListener(ApplicationReadyEvent.class) // 모든 준비가 완료되었을 때 발생하는 이벤트. transactional 을 이용한 aop 적용 가능.
    @Transactional
    public void initDB(){
        log.info("Initialize database");
        initRole();
        initTestAdmin();
        initTestMember();
    }

    private void initRole(){
        roleRepository.saveAll(
                List.of(RoleType.values()).stream()
                        .map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        memberRepository.save(
                new Member("admin", encoder.encode("admin123!"), "어드민",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initTestMember() {
        memberRepository.save(
                new Member("member", encoder.encode("member123!"), "멤버",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new)))
        );
    }
}
