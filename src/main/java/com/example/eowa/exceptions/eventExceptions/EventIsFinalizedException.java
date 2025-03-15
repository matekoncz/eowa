package com.example.eowa.exceptions.eventExceptions;

public class EventIsFinalizedException extends EventException{

    public EventIsFinalizedException() {
        super("This event cannot be modified, try to un-finalize it.");
    }
}
