package com.booking.strategy;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.model.Popcorn;
import com.booking.model.Seat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockPreventionBookingStrategy implements BookingStrategy {

    @Override
    public void execute(BookingSystem bookingSystem, int threadId) {
        int pairIndex = bookingSystem.getRandom().nextInt(SimulationConfig.TOTAL_SEATS);

        Seat targetSeat = bookingSystem.getSeats().get(pairIndex);
        Popcorn targetPopcorn = bookingSystem.getPopcorns().get(pairIndex);

        ReentrantLock seatLock = bookingSystem.getSeatLock(pairIndex);
        ReentrantLock popcornLock = bookingSystem.getPopcornLock(pairIndex);

        if (targetSeat.isDeadlocked() || targetPopcorn.isDeadlocked() ||
            targetSeat.isBooked() || targetPopcorn.isReserved()) {
            bookingSystem.notifyBookingFailed(threadId);
            return;
        }

        long startTime = System.currentTimeMillis();
        boolean acquiredSeat = false;
        boolean acquiredPopcorn = false;

        try {
            seatLock.lock();
            acquiredSeat = true;
            targetSeat.setHolderThreadId(threadId);

            try {
                Thread.sleep(SimulationConfig.LOCK_ACQUISITION_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                acquiredPopcorn = popcornLock.tryLock(
                        SimulationConfig.DEADLOCK_TIMEOUT_MS,
                        TimeUnit.MILLISECONDS
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (acquiredPopcorn) {
                if (targetSeat.isDeadlocked() || targetPopcorn.isDeadlocked()) {
                    targetSeat.addBookingThread(threadId);
                    long waitTime = System.currentTimeMillis() - startTime;
                    bookingSystem.notifyDeadlockDetected(targetSeat, targetPopcorn, threadId);
                    bookingSystem.notifyThreadStuck(threadId, waitTime);
                    return;
                }
                if (targetSeat.isBooked() || targetPopcorn.isReserved()) {
                    bookingSystem.notifyBookingFailed(threadId);
                    return;
                }
                targetPopcorn.setHolderThreadId(threadId);
                targetSeat.setBooked(true);
                targetSeat.addBookingThread(threadId);
                targetPopcorn.setReserved(true);

                bookingSystem.notifyPairComplete(targetSeat, targetPopcorn, threadId);
                bookingSystem.notifySeatBooked(targetSeat, threadId);

            } else {
                long waitTime = System.currentTimeMillis() - startTime;
                targetSeat.setDeadlocked(true);
                targetPopcorn.setDeadlocked(true);
                targetSeat.addBookingThread(threadId);

                bookingSystem.notifyDeadlockDetected(targetSeat, targetPopcorn, threadId);
                bookingSystem.notifyThreadStuck(threadId, waitTime);
            }

        } finally {
            targetSeat.setHolderThreadId(-1);
            if (acquiredPopcorn) {
                targetPopcorn.setHolderThreadId(-1);
            }

            if (acquiredPopcorn) {
                popcornLock.unlock();
            }
            if (acquiredSeat) {
                seatLock.unlock();
            }
        }
    }
}
