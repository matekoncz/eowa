package com.example.eowa.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User reciever;

    @Column
    private String title;

    @Column
    private String content;

    @Column(name = "isread")
    private boolean read;

    public Mail() {
    }

    public Mail(User sender, User reciever, String title, String content, boolean read) {
        this.sender = sender;
        this.reciever = reciever;
        this.title = title;
        this.content = content;
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mail mail = (Mail) o;
        return id == mail.id && read == mail.read && Objects.equals(sender, mail.sender) && Objects.equals(reciever, mail.reciever) && Objects.equals(title, mail.title) && Objects.equals(content, mail.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, reciever, title);
    }
}
