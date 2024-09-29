package com.example.eowa.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User owner;

    @Column
    private String eventName;

    @ManyToMany
    @JoinTable( name = "participation",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "username"))
    private Set<User> participants;

    public Event() {
    }

    public Event(long id, User owner, String eventName, Set<User> participants) {
        this.id = id;
        this.owner = owner;
        this.eventName = eventName;
        this.participants = participants;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void addParticipant(User participant){
        this.participants.add(participant);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
