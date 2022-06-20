package tutorial.board.domain.account.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board.domain.account.Role;
import tutorial.board.domain.account.RoleType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoleRepositoryTest {

    @Autowired RoleRepository roleRepository;
    @PersistenceContext EntityManager em;

    private Role createRole() {
        return new Role(RoleType.USER);
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    @Test
    void 역할생성() throws Exception {
        //given
        Role role = createRole();

        //when
        roleRepository.save(role);
        clear();

        //then
        Role foundRole = roleRepository.findById(role.getId())
                .orElseThrow(Exception::new);
        assertThat(foundRole.getId()).isEqualTo(role.getId());
    }

    @Test
    void 역할삭제() throws Exception {
        //given
        Role role = roleRepository.save(createRole());
        clear();

        //when
        roleRepository.delete(role);

        //then
        assertThrows(Exception.class,
                () -> roleRepository.findById(role.getId())
                        .orElseThrow(Exception::new));
    }

    @Test
    void 오류_역할내용_중복시() throws Exception {
        //given
        roleRepository.save(createRole());
        clear();

        //when

        //then
        assertThrows(Exception.class,
                () -> roleRepository.save(createRole()));
    }

}