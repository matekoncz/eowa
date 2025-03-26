package com.example.eowa.model;

public class TimeIntervalDetails {
    private long hourSerial;

    private int length;

    private int participantNumber;

    public TimeIntervalDetails() {
    }

    public TimeIntervalDetails(long hourSerial, int length, int participantNumber) {
        this.hourSerial = hourSerial;
        this.length = length;
        this.participantNumber = participantNumber;
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
}
