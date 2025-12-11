package com.booking.core;

import com.booking.config.SimulationConfig;
import com.booking.model.BookingResult;
import com.booking.model.Seat;
import com.booking.model.SimulationStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookingSystem {

    private final List<Seat> seats;
    private final SimulationStats stats;
    private final boolean isSafeMode;
    private final Random random;

    public BookingSystem(int totalSeats, int totalUsers, boolean safeMode) {
        this.seats = new ArrayList<>();

        for (int i = 0; i < totalSeats; i++) {
            seats.add(new Seat(i));
        }

        this.stats = new SimulationStats(totalSeats, totalUsers);
        this.isSafeMode = safeMode;
        this.stats.setSafeMode(safeMode);
        this.random = new Random();
    }

    public boolean isSafeMode() {
        return isSafeMode;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public SimulationStats getStats() {
        return stats;
    }

    public BookingResult bookSeat(int threadId) {
        if (isSafeMode) {
            return bookSeatSafe(threadId);
        } else {
            return bookSeatUnsafe(threadId);
        }
    }

    private synchronized BookingResult bookSeatSafe(int threadId) {
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                seat.setBooked(true);
                seat.addBookingThread(threadId);

                return new BookingResult(threadId, seat.getSeatNumber(), true);
            }
        }
        return new BookingResult(threadId, -1, false);
    }

    private BookingResult bookSeatUnsafe(int threadId) {
        Seat targetSeat = null;
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                targetSeat = seat;
                break;
            }
        }

        if (targetSeat == null) {
            return new BookingResult(threadId, -1, false);
        }

        try {
            int baseDelay = SimulationConfig.UNSAFE_BOOKING_DELAY;
            int randomDelay = random.nextInt(2);
            Thread.sleep(baseDelay + randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new BookingResult(threadId, -1, false);
        }

        targetSeat.setBooked(true);
        targetSeat.addBookingThread(threadId);

        return new BookingResult(threadId, targetSeat.getSeatNumber(), true);
    }

    public void calculateFinalStats() {
        int actualSeatsBooked = 0;
        int actualCollisions = 0;
        int successfulBookings = 0;

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                actualSeatsBooked++;

                if (seat.isHasCollision()) {
                    actualCollisions++;
                }

                successfulBookings += seat.getThreadIds().size();
            }
        }

        stats.setSeatsBooked(actualSeatsBooked);
        stats.setCollisions(actualCollisions);
        stats.setSuccessfulBookings(successfulBookings);
    }

    public void reset() {
        for (Seat seat : seats) {
            seat.reset();
        }

        stats.reset();
    }
}