package tutorial.board2.domain.account.exception;

public class MemberUsernameAlreadyExistsException extends RuntimeException{
    public MemberUsernameAlreadyExistsException(String message){
        super(message);
    }
}
