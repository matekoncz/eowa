package com.example.eowa.model;

import java.util.Set;

public class TimeIntervalDetails {
    private long hourSerial;

    private int length;

    private int participantNumber;

    private Set<User> participants;

    public TimeIntervalDetails() {
    }

    public TimeIntervalDetails(long hourSerial, int length, int participantNumber, Set<User> participants) {
        this.hourSerial = hourSerial;
        this.length = length;
        this.participantNumber = participantNumber;
        this.participants = participants;
    }

    public long getHourSerial() {
        return hourSerial;
    }

    public void setHourSerial(long hourSerial) {
        this.hourSerial = hourSerial;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public void setParticipantNumber(int participantNumber) {
        this.participantNumber = participantNumber;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
