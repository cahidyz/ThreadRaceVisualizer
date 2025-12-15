package com.booking.core;

import com.booking.config.SimulationConfig;
import com.booking.model.BookingResult;
import com.booking.model.Seat;
import com.booking.model.SimulationStats;
import com.booking.observer.BookingObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BookingSystem {

    private final List<Seat> seats;
    private final SimulationStats stats;
    private final boolean isSafeMode;
    private final Random random;
    private final List<BookingObserver> observers;

    public BookingSystem(int totalSeats, int totalUsers, boolean safeMode) {
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

    public BookingResult bookSeat(int threadId) {
        if (isSafeMode) {
            return bookSeatSafe(threadId);
        } else {
            return bookSeatUnsafe(threadId);
        }
    }

    private synchronized BookingResult bookSeatSafe(int threadId) {
        return seats.stream()
                .filter(seat -> !seat.isBooked())
                .findFirst()
                .map(seat -> {
                    seat.setBooked(true);
                    seat.addBookingThread(threadId);
                    notifyObservers(observer -> observer.onSeatBooked(seat, threadId));
                    return new BookingResult(threadId, seat.getSeatNumber(), true);
                })
                .orElseGet(() -> {
                    notifyObservers(observer -> observer.onBookingFailed(threadId));
                    return new BookingResult(threadId, -1, false);
                });
    }

    private BookingResult bookSeatUnsafe(int threadId) {
        List<Seat> emptySeats = seats.stream()
                .filter(seat -> !seat.isBooked())
                .toList();

        if (emptySeats.isEmpty()) {
            notifyObservers(observer -> observer.onBookingFailed(threadId));
            return new BookingResult(threadId, -1, false);
        }

        Seat targetSeat = emptySeats.get(random.nextInt(emptySeats.size()));

        try {
            int baseDelay = SimulationConfig.UNSAFE_BOOKING_DELAY;
            int randomDelay = random.nextInt(2);
            Thread.sleep(baseDelay + randomDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            notifyObservers(observer -> observer.onBookingFailed(threadId));
            return new BookingResult(threadId, -1, false);
        }

        boolean wasAlreadyBooked = targetSeat.isBooked();

        targetSeat.setBooked(true);
        targetSeat.addBookingThread(threadId);

        if (wasAlreadyBooked || targetSeat.getThreadIds().size() > 1) {
            targetSeat.setHasCollision(true);
            notifyObservers(observer -> observer.onCollisionDetected(targetSeat));
        }

        notifyObservers(observer -> observer.onSeatBooked(targetSeat, threadId));

        return new BookingResult(threadId, targetSeat.getSeatNumber(), true);
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

    private void notifyObservers(Consumer<BookingObserver> notification) {
        for (BookingObserver observer : observers) {
            try {
                notification.accept(observer);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
}