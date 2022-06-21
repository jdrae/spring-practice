package tutorial.board2.factory.dto;

import tutorial.board2.domain.account.dto.SignUpRequest;

public class SignUpRequestFactory {

    public static SignUpRequest createSignUpRequest() {
        return new SignUpRequest("username", "123456a!",  "nickname");
    }

    public static SignUpRequest createSignUpRequest(String password, String username, String nickname) {
        return new SignUpRequest( username, password,nickname);
    }

    public static SignUpRequest createSignUpRequestWithPassword(String password) {
        return new SignUpRequest("username", password,  "nickname");
    }

    public static SignUpRequest createSignUpRequestWithUsername(String username) {
        return new SignUpRequest( username, "123456a!", "nickname");
    }

    public static SignUpRequest createSignUpRequestWithNickname(String nickname) {
        return new SignUpRequest("username","123456a!", nickname);
    }
}