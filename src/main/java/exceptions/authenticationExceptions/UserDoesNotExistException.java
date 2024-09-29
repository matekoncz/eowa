package exceptions.authenticationExceptions;

public class UserDoesNotExistException extends AuthenticationException {
    public UserDoesNotExistException() {
        super("User does not exist");
    }
}