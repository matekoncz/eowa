package com.example.eowa.model;

import jakarta.persistence.*;

@Entity
public class Session {

    @Id
    private String jsessionid;

    @OneToOne(mappedBy = "session")
    private User user;
    @Column
    private long timestamp;

    public Session() {
    }

    public Session(User user, long timestamp, String jsessionid) {
        this.user = user;
        this.timestamp = timestamp;
        this.jsessionid = jsessionid;
    }


    public User getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
