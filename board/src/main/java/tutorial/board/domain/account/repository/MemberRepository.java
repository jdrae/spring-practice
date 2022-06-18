package tutorial.board.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutorial.board.domain.account.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
}
