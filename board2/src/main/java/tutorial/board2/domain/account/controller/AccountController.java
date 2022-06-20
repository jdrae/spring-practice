package tutorial.board2.domain.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tutorial.board2.domain.account.dto.SignInRequest;
import tutorial.board2.domain.account.dto.SignUpRequest;
import tutorial.board2.domain.account.service.AccountService;
import tutorial.board2.global.response.Response;

import javax.validation.Valid;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest req){
        accountService.signUp(req);
        return Response.success();
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest req){
        return Response.success(accountService.signIn(req));
    }
}
