package com.example.eowa.exceptions.authenticationExceptions;

public class UserIsNotEventOwnerException extends AuthenticationException{
    public UserIsNotEventOwnerException(){
        super("The current user is not the Owner of this Event");
    }
}
