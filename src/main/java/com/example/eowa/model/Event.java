package com.example.eowa.model;

import jakarta.persistence.*;
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

    @Column(name = "code")
    private String invitationCode;

    public Event() {
    }

    public Event( User owner, String eventName, Set<User> participants, String description) {
        this.owner = owner;
        this.eventName = eventName;
        this.participants = participants;
        this.description = description;
        this.invitationCode = eventName+"::"+ UUID.randomUUID();
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
}
