package tutorial.board2.factory.dto;

import tutorial.board2.domain.account.dto.SignInRequest;

public class SignInRequestFactory {
    public static SignInRequest createSignInRequest() {
        return new SignInRequest("username", "123456a!");
    }

    public static SignInRequest createSignInRequest(String username, String password) {
        return new SignInRequest(username, password);
    }

    public static SignInRequest createSignInRequestWithUsername(String username) {
        return new SignInRequest(username, "123456a!");
    }

    public static SignInRequest createSignInRequestWithPassword(String password) {
        return new SignInRequest("username", password);
    }

}
