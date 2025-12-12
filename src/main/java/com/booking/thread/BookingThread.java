package com.booking.thread;

import com.booking.core.BookingSystem;
import com.booking.model.BookingResult;

public class BookingThread implements Runnable {
    private final BookingSystem bookingSystem;
    private final int threadId;
    private BookingResult result;

    public BookingThread(BookingSystem bookingSystem, int threadId) {
        this.bookingSystem = bookingSystem;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        result = bookingSystem.bookSeat(threadId);
    }

    public BookingResult getResult() {
        return result;
    }

    public int getThreadId() {
        return threadId;
    }
}