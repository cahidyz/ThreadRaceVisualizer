package com.booking.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Seat {
    private final int seatNumber;
    private volatile boolean isBooked;
    private volatile boolean hasCollision;
    private volatile boolean isDeadlocked;
    private volatile int holderThreadId;
    private final List<Integer> threadIds;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isBooked = false;
        this.hasCollision = false;
        this.isDeadlocked = false;
        this.holderThreadId = -1;
        this.threadIds = new CopyOnWriteArrayList<>();
    }

    public void addBookingThread(int threadId) {
        threadIds.add(threadId);

        if (threadIds.size() > 1) {
            hasCollision = true;
        }
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public boolean isHasCollision() {
        return hasCollision;
    }

    public List<Integer> getThreadIds() {
        return threadIds;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public void setHasCollision(boolean hasCollision) {
        this.hasCollision = hasCollision;
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
        isBooked = false;
        threadIds.clear();
        hasCollision = false;
        isDeadlocked = false;
        holderThreadId = -1;
    }
}
