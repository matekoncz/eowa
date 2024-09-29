package exceptions.userExceptions;

public class UserMissingRequiredFieldsException extends UserException{
    public UserMissingRequiredFieldsException() {
        super("User object is missing required fields: Username, Password, Email must be set");
    }
}
