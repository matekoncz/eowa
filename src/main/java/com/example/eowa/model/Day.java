package com.example.eowa.model;

import jakarta.persistence.*;

import java.time.LocalDate;
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
    @Column
    private boolean hasExtraHour;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Hour> hours;

    public Day() {
    }

    public Day(ZonedDateTime dayStartTime, int serialNumber, boolean enabled, boolean hasExtraHour, List<Hour> hours) {
        this.dayStartTime = dayStartTime;
        this.serialNumber = serialNumber;
        this.enabled = enabled;
        this.hasExtraHour = hasExtraHour;
        this.hours = hours;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(ZonedDateTime dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHasExtraHour() {
        return hasExtraHour;
    }

    public void setHasExtraHour(boolean hasExtraHour) {
        this.hasExtraHour = hasExtraHour;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }
}
