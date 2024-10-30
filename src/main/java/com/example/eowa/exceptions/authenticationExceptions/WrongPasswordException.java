package com.example.eowa.exceptions.authenticationExceptions;

public class WrongPasswordException extends AuthenticationException{

    public WrongPasswordException() {
        super("Invalid password");
    }
}
