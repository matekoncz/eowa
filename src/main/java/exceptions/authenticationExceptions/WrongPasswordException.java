package exceptions.authenticationExceptions;

public class WrongPasswordException extends AuthenticationException{

    public WrongPasswordException() {
        super("Invalid password");
    }
}
