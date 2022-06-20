package tutorial.board.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@Slf4j
@RestControllerAdvice //@ControllerAdvice + @ResponseBody
// @ControllerAdvice는 모든  @Controller에서 발생하는 예외를 처리. (filter 의 예외는 넘어오지 않음)
// 그리고 @ExceptionHandler를 통해, 어떤 예외를 잡아서 처리할 지 명시.
public class ExceptionAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception){
        log.error("BaseException errorMesssage(): {}", exception.getExceptionType().getErrorMessage());
        log.error("BaseException errorCode(): {}",exception.getExceptionType().getErrorCode());

        return new ResponseEntity(new ExceptionDto(exception.getExceptionType().getErrorCode()),exception.getExceptionType().getHttpStatus());
    }

    //@Valid 에서 예외 발생
    @ExceptionHandler(BindException.class)
    public ResponseEntity handleValidEx(BindException exception){

        log.error("@ValidException 발생! {}", exception.getMessage() );
        return new ResponseEntity(new ExceptionDto(2000),HttpStatus.BAD_REQUEST);
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadableExceptionEx(HttpMessageNotReadableException exception){

        log.error("Json을 파싱하는 과정에서 예외 발생! {}", exception.getMessage() );
        return new ResponseEntity(new ExceptionDto(3000),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception){
        exception.printStackTrace();
        // validation 예외의 상태코드를 200으로 하기 위함.
        return new ResponseEntity(HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
    }
}
