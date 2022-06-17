package tutorial.jwtauth.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tutorial.jwtauth.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities") // lazy 가 아닌 eager 로 authorities 정보를 같이 가져온다
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
