package com.booking.strategy;

import com.booking.core.BookingSystem;
import com.booking.model.Seat;

public class SynchronizedBookingStrategy implements BookingStrategy {

    @Override
    public synchronized void execute(BookingSystem bookingSystem, int threadId) {
        Seat seat = bookingSystem.getSeats().stream()
                .filter(s -> !s.isBooked())
                .findFirst()
                .orElse(null);

        if (seat != null) {
            seat.setBooked(true);
            seat.addBookingThread(threadId);
            bookingSystem.notifySeatBooked(seat, threadId);
        } else {
            bookingSystem.notifyBookingFailed(threadId);
        }
    }
}
