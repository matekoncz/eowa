package exceptions.authenticationExceptions;

public class InvalidSessionException extends AuthenticationException{

    public InvalidSessionException() {
        super("Session expired");
    }
}
