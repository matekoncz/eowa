package com.example.eowa.model;

import jakarta.persistence.*;

@Entity
public class EventBlueprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @ManyToOne
    private User insertUser;

    @Column(columnDefinition = "TEXT")
    private String content;

    public EventBlueprint() {
    }

    public EventBlueprint(String name, String content,User insertUser) {
        this.name = name;
        this.content = content;
        this.insertUser = insertUser;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getInsertUser() {
        return insertUser;
    }

    public void setInsertUser(User insertUser) {
        this.insertUser = insertUser;
    }
}
