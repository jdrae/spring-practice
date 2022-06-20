package tutorial.board2.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignInResponse;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.exception.LoginFailureException;
import tutorial.board2.domain.account.exception.MemberNicknameAlreadyExistsException;
import tutorial.board2.domain.account.exception.MemberUsernameAlreadyExistsException;
import tutorial.board2.domain.account.exception.RoleNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;
import tutorial.board2.domain.account.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService; // spring security

    @Transactional
    public void signUp(SignUpRequest dto){
        validateSignUpInfo(dto);
        memberRepository.save(SignUpRequest.toEntity(dto, getRole(), passwordEncoder));
    }

    public SignInResponse signIn(SignInRequest req){
        Member member = memberRepository.findByUsername(req.getUsername())
                .orElseThrow(LoginFailureException::new);
        validatePasword(req, member);
        String subject = createSubject(member);
        String accessToken = tokenService.createAccessToken(subject);
        String refreshToken = tokenService.createRefreshToken(subject);
        return new SignInResponse(accessToken, refreshToken);
    }


    //== helper methods ==//
    private Role getRole() {
        return roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
    }

    private void validateSignUpInfo(SignUpRequest dto){
        if (memberRepository.existsByUsername(dto.getUsername()))
            throw new MemberUsernameAlreadyExistsException(dto.getUsername());
        if (memberRepository.existsByNickname(dto.getNickname()))
            throw new MemberNicknameAlreadyExistsException(dto.getNickname());
    }

    private void validatePasword(SignInRequest req, Member member){
        if (!passwordEncoder.matches(req.getPassword(), member.getPassword())){
            throw new LoginFailureException();
        }
    }

    private String createSubject(Member member){
        return String.valueOf(member.getId());
    }

}
