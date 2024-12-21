package com.example.eowa.exceptions.authenticationExceptions;

public class InvalidInvitationCodeException extends AuthenticationException{
    public InvalidInvitationCodeException(){
        super("Invitation Code is invalid");
    }
}
