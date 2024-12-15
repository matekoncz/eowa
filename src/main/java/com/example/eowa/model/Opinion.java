package com.example.eowa.model;

import jakarta.persistence.*;

@Entity
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @Column
    private UserOpinion userOpinion;

    public Opinion() {
    }

    public Opinion(User user, UserOpinion userOpinion) {
        this.user = user;
        this.userOpinion = userOpinion;
    }

    public long getId() {
        return id;
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
