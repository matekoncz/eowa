package com.example.eowa.model;

public class MomentDetails {
    private long hourId;

    private int length;

    private int participantNumber;

    public MomentDetails() {
    }

    public MomentDetails(long hourId, int length, int participantNumber) {
        this.hourId = hourId;
        this.length = length;
        this.participantNumber = participantNumber;
    }

    public long getHourId() {
        return hourId;
    }

    public void setHourId(long hourId) {
        this.hourId = hourId;
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
