package com.example.eowa.model;

import com.example.eowa.controller.ZoneIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZoneIdSerializer;
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
    @JsonSerialize(using = ZoneIdSerializer.class)
    @JsonDeserialize(using = ZoneIdDeserializer.class)
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
