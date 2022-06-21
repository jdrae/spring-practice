package tutorial.board2.domain.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.service.AccountService;
import tutorial.board2.global.response.Response;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req){
        log.info("회원가입 요청");
        accountService.signUp(req);
        return Response.success();
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest req){
        return Response.success(accountService.signIn(req));
    }

    //== refresh token ==//
    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken(@RequestHeader(value = "Authorization") String refreshToken) {
        // 기본 required = true 기 때문에 없으면 MissingRequestHeaderException 예외 발생
        return Response.success(accountService.refreshAccessToken(refreshToken));
    }
}
