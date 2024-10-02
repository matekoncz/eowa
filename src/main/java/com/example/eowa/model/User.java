package com.example.eowa.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity(name="eowauser")
public class User {
    @Id
    private String username;
    @Column
    private String password;
    @Column
    private String email;

    @OneToOne(mappedBy = "user")
    private Session session;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "participants")
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
