package com.example.eowa.exceptions;

public class CookieDoesNotExistException extends Exception{
    public CookieDoesNotExistException(String cookieName){
        super("Required cookie does not exist: "+cookieName);
    }
}
