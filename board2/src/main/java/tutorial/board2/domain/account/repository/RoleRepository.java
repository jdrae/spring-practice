package tutorial.board2.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}