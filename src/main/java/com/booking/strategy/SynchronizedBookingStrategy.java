package com.booking.strategy;

import com.booking.core.BookingSystem;
import com.booking.observer.BookingObserver;

public class SynchronizedBookingStrategy implements BookingStrategy {

    @Override
    public synchronized void execute(BookingSystem bookingSystem, int threadId) {
        bookingSystem.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .findFirst()
                .ifPresentOrElse(
                        seat -> {
                            seat.setBooked(true);
                            seat.addBookingThread(threadId);
                            bookingSystem.notifyObservers(observer -> observer.onSeatBooked(seat, threadId));
                        },
                        () -> bookingSystem.notifyObservers(observer -> observer.onBookingFailed(threadId))
                );
    }
}
