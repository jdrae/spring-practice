package tutorial.board2.domain.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tutorial.board2.domain.account.service.MemberService;
import tutorial.board2.global.response.Response;

@Api(value = "Member Controller", tags = "Member")
@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @ApiOperation(value = "사용자 정보 조회")
    @GetMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@ApiParam(value = "사용자 id", required = true) @PathVariable Long id){
        return Response.success(memberService.read(id));
    }

    @ApiOperation(value = "사용자 정보 삭제")
    @DeleteMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "사용자 id", required = true) @PathVariable Long id){
        memberService.delete(id);
        return Response.success();
    }

}
