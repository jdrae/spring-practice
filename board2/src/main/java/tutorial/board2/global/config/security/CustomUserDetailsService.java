package tutorial.board2.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.MemberRole;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // pk 를 통해 접근하도록 비용을 줄임.
        // Redis와 같은 메모리 DB에 사용자 정보를 짧은 시간 캐시하는 등의 방법도 있다.

        // 로그인하여 토큰을 발급받았지만 계정을 삭제하면, 토큰은 유효하지만 사용자는 없는 경우가 됨.
        // 사용자 정보와 권한이 없는 Member -> UserDetails 반환
        Member member = memberRepository.findById(Long.valueOf(userId))
                .orElseGet(() -> new Member(null, null, null, List.of()));

        return new CustomUserDetails(
                String.valueOf(member.getId()),
                member.getRoles().stream().map(MemberRole::getRole)
                        .map(Role::getRoleType)
                        .map(Enum::toString)
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}
