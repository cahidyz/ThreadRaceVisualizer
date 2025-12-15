package com.booking.core;

import com.booking.config.SimulationConfig;
import com.booking.model.Seat;
import com.booking.model.SimulationStats;
import com.booking.observer.BookingObserver;
import com.booking.strategy.BookingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class BookingSystem {

    private final List<Seat> seats;
    private final SimulationStats stats;
    private final boolean isSafeMode;
    private final Random random;
    private final List<BookingObserver> observers;
    private final BookingStrategy  bookingStrategy;

    public BookingSystem(int totalSeats, int totalUsers, boolean safeMode, BookingStrategy bookingStrategy) {
        this.bookingStrategy = bookingStrategy;
        this.seats = new ArrayList<>();

        for (int i = 0; i < totalSeats; i++) {
            seats.add(new Seat(i));
        }

        this.stats = new SimulationStats(totalSeats, totalUsers);
        this.isSafeMode = safeMode;
        this.stats.setSafeMode(safeMode);
        this.random = new Random();
        this.observers = new ArrayList<>();
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

    public void bookSeat(int threadId) {
        bookingStrategy.execute(this, threadId);
    }

    public void calculateFinalStats() {
        List<Seat> bookedSeats = seats.stream()
                .filter(Seat::isBooked)
                .toList();

        int actualSeatsBooked = bookedSeats.size();
        long actualCollisions = bookedSeats.stream()
                .filter(Seat::isHasCollision)
                .count();
        int successfulBookings = (int) (actualSeatsBooked - actualCollisions);
        int totalBookingAttempts = bookedSeats.stream()
                .mapToInt(seat -> seat.getThreadIds().size())
                .sum();

        int oversoldCount = Math.max(0, totalBookingAttempts - stats.getTotalSeats());

        stats.setSeatsBooked(actualSeatsBooked);
        stats.setCollisions((int) actualCollisions);
        stats.setSuccessfulBookings(successfulBookings);
        stats.setOversoldCount(oversoldCount);
    }

    public void reset() {
        for (Seat seat : seats) {
            seat.reset();
        }

        stats.reset();
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    public Random getRandom() {
        return random;
    }

    public void notifyObservers(Consumer<BookingObserver> notification) {
        for (BookingObserver observer : observers) {
            try {
                notification.accept(observer);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
}