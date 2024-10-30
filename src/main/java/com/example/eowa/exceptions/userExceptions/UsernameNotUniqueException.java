package com.example.eowa.exceptions.userExceptions;

public class UsernameNotUniqueException extends UserException{
    public UsernameNotUniqueException(){
        super("Username is already taken");
    }
}
