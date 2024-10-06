package exceptions.userExceptions;

public class EmailAddressNotUniqueException extends UserException{
    public EmailAddressNotUniqueException(){
        super("This email address is already taken");
    }
}
