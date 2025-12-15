package com.booking.strategy;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.model.Seat;
import com.booking.observer.BookingObserver;

import java.util.List;

public class RaceConditionSimulationStrategy implements BookingStrategy {

    @Override
    public void execute(BookingSystem bookingSystem, int threadId) {
        List<Seat> emptySeats = bookingSystem.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .toList();

        if (emptySeats.isEmpty()) {
            bookingSystem.notifyObservers(observer -> observer.onBookingFailed(threadId));
            return;
        }

        Seat targetSeat = emptySeats.get(bookingSystem.getRandom().nextInt(emptySeats.size()));

        try {
            int baseDelay = SimulationConfig.UNSAFE_BOOKING_DELAY;
            int randomDelay = bookingSystem.getRandom().nextInt(2);
            Thread.sleep(baseDelay + randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            bookingSystem.notifyObservers(observer -> observer.onBookingFailed(threadId));
            return;
        }

        boolean wasAlreadyBooked = targetSeat.isBooked();

        targetSeat.setBooked(true);
        targetSeat.addBookingThread(threadId);

        if (wasAlreadyBooked || targetSeat.getThreadIds().size() > 1) {
            targetSeat.setHasCollision(true);
            bookingSystem.notifyObservers(observer -> observer.onCollisionDetected(targetSeat));
        }

        bookingSystem.notifyObservers(observer -> observer.onSeatBooked(targetSeat, threadId));
    }
}
