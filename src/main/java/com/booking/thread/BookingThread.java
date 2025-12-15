package com.booking.thread;

import com.booking.core.BookingSystem;

public class BookingThread implements Runnable {
    private final BookingSystem bookingSystem;
    private final int threadId;

    public BookingThread(BookingSystem bookingSystem, int threadId) {
        this.bookingSystem = bookingSystem;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        bookingSystem.bookSeat(threadId);
    }

    public int getThreadId() {
        return threadId;
    }
}