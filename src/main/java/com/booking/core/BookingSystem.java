package com.booking.core;

import com.booking.config.SimulationConfig;
import com.booking.model.Popcorn;
import com.booking.model.Seat;
import com.booking.model.SimulationStats;
import com.booking.observer.BookingObserver;
import com.booking.strategy.BookingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class BookingSystem {

    private final List<Seat> seats;
    private final SimulationStats stats;
    private final boolean isSafeMode;
    private final Random random;
    private final List<BookingObserver> observers;
    private final BookingStrategy bookingStrategy;
    private final List<Popcorn> popcorns;
    private final boolean isDeadlockMode;
    private final ReentrantLock[] seatLocks;
    private final ReentrantLock[] popcornLocks;

    public BookingSystem(int totalSeats, int totalUsers, boolean safeMode, BookingStrategy bookingStrategy) {
        this(totalSeats, totalUsers, safeMode, bookingStrategy, false);
    }

    public BookingSystem(int totalSeats, int totalUsers, boolean safeMode, BookingStrategy bookingStrategy, boolean deadlockMode) {
        this.bookingStrategy = bookingStrategy;
        this.isDeadlockMode = deadlockMode;
        this.seats = new ArrayList<>();

        for (int i = 0; i < totalSeats; i++) {
            seats.add(new Seat(i));
        }

        this.stats = new SimulationStats(totalSeats, totalUsers);
        this.isSafeMode = safeMode;
        this.stats.setSafeMode(safeMode);
        this.stats.setDeadlockMode(deadlockMode);
        this.random = new Random();
        this.observers = new ArrayList<>();

        if (deadlockMode) {
            this.popcorns = new ArrayList<>();
            this.seatLocks = new ReentrantLock[totalSeats];
            this.popcornLocks = new ReentrantLock[totalSeats];

            for (int i = 0; i < totalSeats; i++) {
                popcorns.add(new Popcorn(i));
                seatLocks[i] = new ReentrantLock();
                popcornLocks[i] = new ReentrantLock();
            }
        } else {
            this.popcorns = null;
            this.seatLocks = null;
            this.popcornLocks = null;
        }
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

    public List<Popcorn> getPopcorns() {
        return popcorns;
    }

    public boolean isDeadlockMode() {
        return isDeadlockMode;
    }

    public ReentrantLock getSeatLock(int index) {
        return seatLocks != null ? seatLocks[index] : null;
    }

    public ReentrantLock getPopcornLock(int index) {
        return popcornLocks != null ? popcornLocks[index] : null;
    }

    public void bookSeat(int threadId) {
        bookingStrategy.execute(this, threadId);
    }

    public void calculateFinalStats() {
        if (isDeadlockMode) {
            calculateDeadlockStats();
        } else {
            calculateNormalStats();
        }
    }

    private void calculateNormalStats() {
        List<Seat> bookedSeats = seats.stream()
                .filter(Seat::isBooked)
                .toList();

        int actualSeatsBooked = bookedSeats.size();
        long actualCollisions = bookedSeats.stream()
                .filter(Seat::isHasCollision)
                .count();
        int successfulBookings = (int) (actualSeatsBooked - actualCollisions);

        int totalBookingAttempts = 0;
        for (Seat seat : bookedSeats) {
            totalBookingAttempts += seat.getThreadIds().size();
        }

        int oversoldCount = Math.max(0, totalBookingAttempts - stats.getTotalSeats());

        stats.setSeatsBooked(actualSeatsBooked);
        stats.setCollisions((int) actualCollisions);
        stats.setSuccessfulBookings(successfulBookings);
        stats.setOversoldCount(oversoldCount);
    }

    private void calculateDeadlockStats() {
        int pairsComplete = 0;
        int deadlocks = 0;
        int threadsStuck = 0;
        int seatsBooked = 0;

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                seatsBooked++;
                if (!seat.isDeadlocked()) {
                    pairsComplete++;
                }
            }
            if (seat.isDeadlocked()) {
                deadlocks++;
                threadsStuck += seat.getThreadIds().size();
            }
        }

        int popcornsReserved = 0;
        if (popcorns != null) {
            for (Popcorn popcorn : popcorns) {
                if (popcorn.isReserved()) {
                    popcornsReserved++;
                }
            }
        }

        stats.setSeatsBooked(seatsBooked);
        stats.setPairsComplete(pairsComplete);
        stats.setDeadlocksDetected(deadlocks);
        stats.setThreadsStuck(threadsStuck);
        stats.setPopcornsReserved(popcornsReserved);
    }

    public void reset() {
        for (Seat seat : seats) {
            seat.reset();
        }

        if (popcorns != null) {
            for (Popcorn popcorn : popcorns) {
                popcorn.reset();
            }
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

    public void notifySeatBooked(Seat seat, int threadId) {
        for (BookingObserver observer : observers) {
            try {
                observer.onSeatBooked(seat, threadId);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    public void notifyBookingFailed(int threadId) {
        for (BookingObserver observer : observers) {
            try {
                observer.onBookingFailed(threadId);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    public void notifyCollisionDetected(Seat seat) {
        for (BookingObserver observer : observers) {
            try {
                observer.onCollisionDetected(seat);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    public void notifyDeadlockDetected(Seat seat, Popcorn popcorn, int threadId) {
        for (BookingObserver observer : observers) {
            try {
                observer.onDeadlockDetected(seat, popcorn, threadId);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    public void notifyPairComplete(Seat seat, Popcorn popcorn, int threadId) {
        for (BookingObserver observer : observers) {
            try {
                observer.onPairComplete(seat, popcorn, threadId);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }

    public void notifyThreadStuck(int threadId, long waitTimeMs) {
        for (BookingObserver observer : observers) {
            try {
                observer.onThreadStuck(threadId, waitTimeMs);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
}