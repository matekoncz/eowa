package com.example.eowa.exceptions.CalendarExceptions;

public class TimeTravelException extends CalendarException{
    public TimeTravelException(){
        super("No time traveling! The event can not happen in the past");
    }

}
