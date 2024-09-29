package exceptions.userExceptions;

public class PasswordTooShortException extends UserException {
    public PasswordTooShortException(){
        super("Password must be at least 8 characters long");
    }
}
