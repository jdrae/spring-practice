package tutorial.board2.global.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tutorial.board2.domain.account.exception.*;
import tutorial.board2.global.exception.AccessDeniedException;
import tutorial.board2.global.exception.AuthenticationEntryPointException;
import tutorial.board2.global.response.Response;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e){
        return getFailureResponse("exception.code", "exception.msg");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response authenticationEntryPoint() {
        return getFailureResponse("authenticationEntryPoint.code", "authenticationEntryPoint.msg");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response accessDeniedException() {
        return getFailureResponse("accessDeniedException.code", "accessDeniedException.msg");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(BindException e) {
        return getFailureResponse("bindException.code", "bindException.msg", e.getBindingResult().getFieldError().getDefaultMessage());
    }

    //== 로그인 예외 ==//
    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response loginFailureException() {
        return getFailureResponse("loginFailureException.code", "loginFailureException.msg");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response missingRequestHeaderException(MissingRequestHeaderException e) {
        return getFailureResponse("missingRequestHeaderException.code", "missingRequestHeaderException.msg");
    }

    //== 회원가입 예외 ==//
    @ExceptionHandler(MemberUsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberUsernameAlreadyExistsException(MemberUsernameAlreadyExistsException e) {
        return getFailureResponse("memberUsernameAlreadyExistsException.code", "memberUsernameAlreadyExistsException.msg");
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNicknameAlreadyExistsException(MemberNicknameAlreadyExistsException e) {
        return getFailureResponse("memberNicknameAlreadyExistsException.code", "memberNicknameAlreadyExistsException.msg");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException() {
        return getFailureResponse("memberNotFoundException.code", "memberNotFoundException.msg");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response roleNotFoundException() {
        return getFailureResponse("roleNotFoundException.code", "roleNotFoundException.msg");
    }

    //== helper ==//
    private Response getFailureResponse(String codeKey, String messageKey){
        log.info("code = {}, msg = {}", getCode(codeKey), getMessage(messageKey, null));
        return Response.failure(getCode(codeKey), getMessage(messageKey, null));
    }

    private Response getFailureResponse(String codeKey, String messageKey, Object... args){
        return Response.failure(getCode(codeKey), getMessage(messageKey, args));
    }

    private Integer getCode(String key){
        return Integer.valueOf(messageSource.getMessage(key, null, null));
    }

    private String getMessage(String key, Object... args){
        // 헤더의 로컬 정보(Accept Language)에 따라 메세지 언어 변경
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
