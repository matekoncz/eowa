package com.example.eowa.exceptions.CalendarExceptions;

public class WrongIntervalException extends CalendarException{
    public WrongIntervalException(){
        super("Wrong Interval: the maximum number of days is 90, and the interval can not be negative");
    }
}
