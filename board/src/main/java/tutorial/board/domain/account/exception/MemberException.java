package tutorial.board.domain.account.exception;

import tutorial.board.global.exception.*;

public class MemberException extends BaseException {
    private BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType){
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
