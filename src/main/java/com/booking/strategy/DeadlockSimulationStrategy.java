package com.booking.strategy;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.model.Popcorn;
import com.booking.model.Seat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockSimulationStrategy implements BookingStrategy {

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

        boolean seatFirst = bookingSystem.getRandom().nextBoolean();

        ReentrantLock firstLock = seatFirst ? seatLock : popcornLock;
        ReentrantLock secondLock = seatFirst ? popcornLock : seatLock;

        long startTime = System.currentTimeMillis();
        boolean acquiredFirst = false;
        boolean acquiredSecond = false;

        try {
            firstLock.lock();
            acquiredFirst = true;

            if (targetSeat.isDeadlocked() || targetPopcorn.isDeadlocked()) {
                return;
            }

            if (seatFirst) {
                targetSeat.setHolderThreadId(threadId);
            } else {
                targetPopcorn.setHolderThreadId(threadId);
            }

            try {
                Thread.sleep(SimulationConfig.LOCK_ACQUISITION_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                acquiredSecond = secondLock.tryLock(
                        SimulationConfig.DEADLOCK_TIMEOUT_MS,
                        TimeUnit.MILLISECONDS
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (acquiredSecond) {
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

                if (seatFirst) {
                    targetPopcorn.setHolderThreadId(threadId);
                } else {
                    targetSeat.setHolderThreadId(threadId);
                }

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
            if (seatFirst) {
                targetSeat.setHolderThreadId(-1);
                if (acquiredSecond) {
                    targetPopcorn.setHolderThreadId(-1);
                }
            } else {
                targetPopcorn.setHolderThreadId(-1);
                if (acquiredSecond) {
                    targetSeat.setHolderThreadId(-1);
                }
            }
            if (acquiredSecond) {
                secondLock.unlock();
            }
            if (acquiredFirst) {
                firstLock.unlock();
            }
        }
    }
}
