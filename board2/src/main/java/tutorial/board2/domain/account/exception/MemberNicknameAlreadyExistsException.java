package tutorial.board2.domain.account.exception;

public class MemberNicknameAlreadyExistsException extends RuntimeException{
    public MemberNicknameAlreadyExistsException(String message){
        super(message);
    }
}
