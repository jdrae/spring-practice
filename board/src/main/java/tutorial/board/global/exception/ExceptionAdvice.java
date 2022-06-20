package tutorial.board.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //@ControllerAdvice + @ResponseBody
// @ControllerAdvice는 모든  @Controller에서 발생하는 예외를 처리. (filter 의 예외는 넘어오지 않음)
// 그리고 @ExceptionHandler를 통해, 어떤 예외를 잡아서 처리할 지 명시.
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception){
        // validation 예외의 상태코드를 200으로 하기 위함.
        return new ResponseEntity(HttpStatus.OK);
    }
}
