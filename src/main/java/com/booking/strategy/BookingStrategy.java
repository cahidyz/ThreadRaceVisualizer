package com.booking.strategy;

import com.booking.core.BookingSystem;

public interface BookingStrategy {
    void execute(BookingSystem bookingSystem, int threadId);
}
