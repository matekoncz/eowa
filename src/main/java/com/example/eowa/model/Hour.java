package com.example.eowa.model;

import jakarta.persistence.*;


import java.util.Set;

@Entity
public class Hour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int number;
    @Column
    private boolean enabled;
    @OneToMany()
    private Set<Opinion> opinions;

    public Hour() {
    }

    public Hour(int number, boolean enabled) {
        this.number = number;
        this.enabled = enabled;
    }
}
