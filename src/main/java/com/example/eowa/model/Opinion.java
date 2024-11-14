package com.example.eowa.model;

import jakarta.persistence.*;

@Entity
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Hour hour;

    @ManyToOne
    private User user;

    @Column
    private UserOpinion userOpinion;

    public Opinion() {
    }

    public Opinion(Hour hour, User user, UserOpinion userOpinion) {
        this.hour = hour;
        this.user = user;
        this.userOpinion = userOpinion;
    }

    public long getId() {
        return id;
    }

    public Hour getHour() {
        return hour;
    }

    public User getUser() {
        return user;
    }

    public UserOpinion getUserOpinion() {
        return userOpinion;
    }

    public void setUserOpinion(UserOpinion userOpinion) {
        this.userOpinion = userOpinion;
    }

    public static enum UserOpinion{
        GOOD,
        BAD,
        TOLERABLE
    }
}
