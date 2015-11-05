package com.maths22.laundryview.data;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class Machine implements Comparable<Machine>, Serializable {
    public static final int NO_TIME = -1;

    private String id;
    private String number;
    private Status status = Status.UNKNOWN;
    private int timeRemaining;

    @Inject
    public Machine() {
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Machine)) {
            return false;
        }
        Machine m = (Machine) o;
        return this.getId().equals(m.getId());
    }

    @Override
    public int compareTo(Machine another) {
        return this.getNumber().compareTo(another.getNumber());
    }
}
