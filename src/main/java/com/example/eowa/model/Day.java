package com.example.eowa.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private ZonedDateTime dayStartTime;
    @Column
    private int serialNumber;
    @Column
    private boolean enabled;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Hour> hours;

    public Day() {
    }

    public Day(ZonedDateTime dayStartTime, int serialNumber, boolean enabled, boolean hasExtraHour, List<Hour> hours) {
        this.dayStartTime = dayStartTime;
        this.serialNumber = serialNumber;
        this.enabled = enabled;
        this.hours = hours;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getDayStartTime() {
        return dayStartTime;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Hour> getHours() {
        return hours;
    }

}
