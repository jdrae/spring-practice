package tutorial.board2.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutorial.board2.domain.account.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByNickname(String nickname);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}