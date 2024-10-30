package com.example.eowa.exceptions.authenticationExceptions;

public class UserIsNotParticipantException extends AuthenticationException{
    public UserIsNotParticipantException(){
        super("The current user is not a participant of this event");
    }
}
