package com.booking.model;

import java.util.ArrayList;
import java.util.List;

public class Seat {
    private final int seatNumber;
    private volatile boolean isBooked;
    private volatile boolean hasCollision;
    private final List<Integer> threadIds;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isBooked = false;
        this.hasCollision = false;
        this.threadIds = new ArrayList<Integer>();
    }

    public void addBookingThread(int threadId) {
        threadIds.add(threadId);

        if (threadIds.size() > 1) {
            hasCollision = true;
        }
    }

    public String getTooltipText() {
        if (threadIds.isEmpty()) {
            return "Empty seat";
        }

        if (hasCollision) {
            StringBuilder sb = new StringBuilder("COLLISION!\nBooked by threads: ");

            for (int i = 0; i < threadIds.size(); i++) {
                if (i > 0) {
                    if (i == threadIds.size() - 1) {
                        sb.append(" and ");
                    }
                    else {
                        sb.append(", ");
                    }
                }
                sb.append("#").append(threadIds.get(i));
            }
            return sb.toString();
        }
        return "Seat booked by Thread #" + threadIds.get(0);
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

    public void reset() {
        isBooked = false;
        threadIds.clear();
        hasCollision = false;
    }
}
