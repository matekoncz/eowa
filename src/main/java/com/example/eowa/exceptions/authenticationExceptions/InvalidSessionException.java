package com.example.eowa.exceptions.authenticationExceptions;

public class InvalidSessionException extends AuthenticationException{

    public InvalidSessionException() {
        super("Session expired");
    }
}
