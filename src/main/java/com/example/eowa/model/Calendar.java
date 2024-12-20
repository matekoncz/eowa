package com.example.eowa.model;

import jakarta.persistence.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private ZoneId timeZone;
    @Column
    private ZonedDateTime startTime;
    @Column
    private ZonedDateTime endTime;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Day> days;

    public Calendar() {
    }

    public Calendar(ZoneId timeZone, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.timeZone = timeZone;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public long getId() {
        return id;
    }
}
