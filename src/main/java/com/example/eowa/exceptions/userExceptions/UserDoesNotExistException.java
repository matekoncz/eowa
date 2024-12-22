package com.example.eowa.exceptions.userExceptions;

public class UserDoesNotExistException extends UserException {
    public UserDoesNotExistException() {
        super("User does not exist");
    }
}