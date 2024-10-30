package com.example.eowa.exceptions.userExceptions;

public class EmailAddressInInvalidFormatException extends UserException {
    public EmailAddressInInvalidFormatException() {
        super("Email address must be in format: example@example.xyz");
    }
}
