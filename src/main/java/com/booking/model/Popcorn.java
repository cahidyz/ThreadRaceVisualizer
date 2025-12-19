package com.booking.model;

public class Popcorn {
    private final int popcornNumber;
    private volatile boolean isReserved;
    private volatile boolean isDeadlocked;
    private volatile int holderThreadId;

    public Popcorn(int popcornNumber) {
        this.popcornNumber = popcornNumber;
        this.isReserved = false;
        this.isDeadlocked = false;
        this.holderThreadId = -1;
    }

    public int getPopcornNumber() {
        return popcornNumber;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public boolean isDeadlocked() {
        return isDeadlocked;
    }

    public void setDeadlocked(boolean deadlocked) {
        isDeadlocked = deadlocked;
    }

    public int getHolderThreadId() {
        return holderThreadId;
    }

    public void setHolderThreadId(int holderThreadId) {
        this.holderThreadId = holderThreadId;
    }

    public void reset() {
        isReserved = false;
        isDeadlocked = false;
        holderThreadId = -1;
    }
}
