package com.example.eowa.exceptions.userExceptions;

import com.example.eowa.model.User;

public class UserException extends Exception{
    public UserException(String msg){
        super(msg);
    }
}
