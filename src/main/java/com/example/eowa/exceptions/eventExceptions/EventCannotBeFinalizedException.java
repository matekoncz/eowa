package com.example.eowa.exceptions.eventExceptions;

public class EventCannotBeFinalizedException extends EventException{

    public EventCannotBeFinalizedException() {
        super("Event cannot be finalized: make sure to set all event fields (calendar and selection fields).");
    }
}
