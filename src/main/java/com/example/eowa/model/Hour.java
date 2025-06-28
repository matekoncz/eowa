package com.example.eowa.model;

import jakarta.persistence.*;


import java.util.HashSet;
import java.util.Set;

@Entity
public class Hour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int number;
    @Column
    private int numberInCalendar;
    @Column
    private boolean enabled;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Opinion> opinions;

    public Hour() {
    }

    public Hour(int number,int numberInCalendar, boolean enabled) {
        this.number = number;
        this.enabled = enabled;
        this.numberInCalendar = numberInCalendar;
        this.opinions = new HashSet<>();
    }

    public int getNumberInCalendar() {
        return numberInCalendar;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getNumber() {
        return number;
    }

    public long getId() {
        return id;
    }

    public void setOpinions(Set<Opinion> opinions) {
        this.opinions = opinions;
    }

    public Set<Opinion> getOpinions() {
        return opinions;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
