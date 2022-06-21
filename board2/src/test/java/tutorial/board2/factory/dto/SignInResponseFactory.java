package tutorial.board2.factory.dto;

import tutorial.board2.domain.account.dto.RefreshTokenResponse;
import tutorial.board2.domain.account.dto.SignInResponse;

public class SignInResponseFactory {
    public static SignInResponse createSignInResponse(String accessToken, String refreshToken) {
        return new SignInResponse(accessToken, refreshToken);
    }

    public static RefreshTokenResponse createRefreshTokenResponse(String accessToken){
        return new RefreshTokenResponse(accessToken);

    }
}