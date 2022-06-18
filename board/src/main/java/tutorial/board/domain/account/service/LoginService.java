package tutorial.board.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tutorial.board.domain.account.Member;
import tutorial.board.domain.account.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRolesInString())
                .build();
    }

}
