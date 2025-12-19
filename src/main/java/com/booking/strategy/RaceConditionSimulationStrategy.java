package com.booking.strategy;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.model.Seat;

import java.util.List;

public class RaceConditionSimulationStrategy implements BookingStrategy {

    @Override
    public void execute(BookingSystem bookingSystem, int threadId) {
        List<Seat> emptySeats = bookingSystem.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .toList();

        if (emptySeats.isEmpty()) {
            bookingSystem.notifyBookingFailed(threadId);
            return;
        }

        Seat targetSeat = emptySeats.get(bookingSystem.getRandom().nextInt(emptySeats.size()));

        try {
            int baseDelay = SimulationConfig.UNSAFE_BOOKING_DELAY;
            int randomDelay = bookingSystem.getRandom().nextInt(2);
            Thread.sleep(baseDelay + randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            bookingSystem.notifyBookingFailed(threadId);
            return;
        }

        boolean wasAlreadyBooked = targetSeat.isBooked();

        targetSeat.setBooked(true);
        targetSeat.addBookingThread(threadId);

        if (wasAlreadyBooked || targetSeat.getThreadIds().size() > 1) {
            targetSeat.setHasCollision(true);
            bookingSystem.notifyCollisionDetected(targetSeat);
        }

        bookingSystem.notifySeatBooked(targetSeat, threadId);
    }
}
