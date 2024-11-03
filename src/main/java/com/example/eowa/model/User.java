package com.example.eowa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name="eowauser")
public class User {
    @Id
    private String username;
    @Column
    @JsonProperty
    private String password;
    @Column
    private String email;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Session session;

    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private Set<Event> events;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.events = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Event> getEvents(){
        return events;
    }

    public void addEvent(Event event){
        events.add(event);
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
