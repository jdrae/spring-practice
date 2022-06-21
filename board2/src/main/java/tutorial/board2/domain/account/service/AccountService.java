package tutorial.board2.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.board2.domain.account.Member;
import tutorial.board2.domain.account.Role;
import tutorial.board2.domain.account.RoleType;
import tutorial.board2.domain.account.dto.RefreshTokenResponse;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignInResponse;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.exception.LoginFailureException;
import tutorial.board2.domain.account.exception.MemberNicknameAlreadyExistsException;
import tutorial.board2.domain.account.exception.MemberUsernameAlreadyExistsException;
import tutorial.board2.domain.account.exception.RoleNotFoundException;
import tutorial.board2.domain.account.repository.MemberRepository;
import tutorial.board2.domain.account.repository.RoleRepository;
import tutorial.board2.global.config.token.TokenHelper;
import tutorial.board2.global.exception.AuthenticationEntryPointException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenHelper accessTokenHelper; // 타입이 동일하면 빈의 이름과 매핑되는 변수명에 빈 주입
    private final TokenHelper refreshTokenHelper;

    @Transactional
    public void signUp(SignUpRequest dto){
        validateSignUpInfo(dto);
        memberRepository.save(SignUpRequest.toEntity(dto, getRole(), passwordEncoder));
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req){
        Member member = memberRepository.findByUsername(req.getUsername())
                .orElseThrow(LoginFailureException::new);
        validatePasword(req, member);
        String subject = createSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refreshToken = refreshTokenHelper.createToken(subject);
        return new SignInResponse(accessToken, refreshToken);
    }

    //== refresh token ==//
    public RefreshTokenResponse refreshAccessToken(String rToken){
        // refresh 토큰을 저장하면, 로그인마다 탈취당한 리프레시 토큰을 무효화 할 수 있다.
        // 하지만 현 프로젝트에서는 무상태성을 위해
        // Member 에서 refresh 토큰을 데이터베이스에 저장을 하지 않기 때문에
        // r 토큰이 탈취당할 경우, access 토큰을 무한정으로 재발급할 수 있다.
        // 따라서 로그인 시 발급받은 리프레시 토큰은 (일주일후) 만료되면 유효하지 않다.
        validateRefreshToken(rToken);
        String subject = refreshTokenHelper.extractSubject(rToken);
        String accessToken = accessTokenHelper.createToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private void validateRefreshToken(String rToken){
        if(!refreshTokenHelper.validate(rToken)){
            throw new AuthenticationEntryPointException();
        }
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
