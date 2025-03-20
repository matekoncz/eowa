package com.example.eowa.exceptions.eventExceptions;

public class BlueprintCannotBeAccessedException extends EventException{
    public BlueprintCannotBeAccessedException(){
        super("You can only use blueprints that were created by you.");
    }
}
