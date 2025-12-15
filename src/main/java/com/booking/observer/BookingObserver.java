package com.booking.observer;

import com.booking.model.Seat;
import com.booking.model.SimulationStats;

public interface BookingObserver {

    void onSeatBooked(Seat seat, int threadId);

    void onBookingFailed(int threadId);

    void onCollisionDetected(Seat seat);

    void onThreadStarted(int threadId);

    void onThreadCompleted(int threadId);

    void onSimulationStarted(int totalThreads, boolean isSafeMode);

    void onSimulationCompleted(SimulationStats stats);

    void onProgressUpdate(int activeThreads, int completedThreads, int totalThreads);
}