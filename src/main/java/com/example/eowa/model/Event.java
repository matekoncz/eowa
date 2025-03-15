package com.example.eowa.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Calendar calendar;

    @ManyToOne
    private User owner;

    @Column
    private String eventName;

    @Column
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> participants;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<SelectionField> selectionFields;

    @Column
    private boolean finalized = false;

    @Column(name = "code")
    private String invitationCode;

    public Event() {
    }

    public Event (User owner, String eventName, Set<User> participants, String description, Set<SelectionField> selectionFields,boolean finalized){
        this.selectionFields = selectionFields;
        this.owner = owner;
        this.eventName = eventName;
        this.participants = participants;
        this.description = description;
        this.invitationCode = eventName+"::"+ UUID.randomUUID();
        this.finalized = finalized;

    }
    public Event( User owner, String eventName, Set<User> participants, String description) {
        this(owner,eventName,participants,description,new HashSet<>(),false);
    }

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public void addALlParticipant(Set<User> participants) {
        this.participants.addAll(participants);
    }

    public void addParticipant(User participant){
        this.participants.add(participant);
    }

    public String getEventName() {
        return eventName;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar){
        this.calendar = calendar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void generateInvitationCode(){
        if(invitationCode == null){
            invitationCode = eventName.replace(' ','-')+"::"+ UUID.randomUUID();
        }
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public Set<SelectionField> getSelectionFields() {
        return selectionFields;
    }

    public void setSelectionFields(Set<SelectionField> selectionFields) {
        this.selectionFields = selectionFields;
    }


    public void addSelectionFields(Set<SelectionField> selectionFields) {
        this.selectionFields.addAll(selectionFields);
    }

    public void removeSelectionFields(Set<SelectionField> selectionfield){
        this.selectionFields.removeAll(selectionfield);
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }
}
