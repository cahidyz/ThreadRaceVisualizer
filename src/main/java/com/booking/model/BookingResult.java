package com.booking.model;

public class BookingResult {
    private final int threadId;
    private final int seatNumber;
    private final boolean success;
    private final long timestamp;

    public BookingResult(int threadId, int seatNumber, boolean success) {
        this.threadId = threadId;
        this.seatNumber = seatNumber;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    public String toLogEntry() {
        String statusString;
        if (success) {
            statusString = "SUCCESS";
        } else {
            statusString = "FAILED";
        }

        return "[" + timestamp + "] Thread #" + threadId + " -> Seat " + seatNumber + " (" + statusString + ")";
    }

    public int getThreadId() {
        return threadId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Thread #%d successfully booked seat %d", threadId, seatNumber);
        } else {
            return String.format("Thread #%d failed to book (no seats available)", threadId);
        }
    }
}
