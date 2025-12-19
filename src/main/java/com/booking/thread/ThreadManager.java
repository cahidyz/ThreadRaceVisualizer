package com.booking.thread;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.model.SimulationStats;
import com.booking.observer.BookingObserver;

import java.util.ArrayList;
import java.util.List;

public class ThreadManager {
    private final BookingSystem bookingSystem;
    private final List<Thread> threads;
    private final List<BookingThread> bookingThreads;
    private volatile boolean isRunning;
    private final List<BookingObserver> observers;

    public ThreadManager(BookingSystem bookingSystem) {
        this.bookingSystem = bookingSystem;
        this.threads = new ArrayList<>();
        this.bookingThreads = new ArrayList<>();
        this.isRunning = false;
        this.observers = new ArrayList<>();
    }

    public void prepareThreads(int totalThreads) {
        threads.clear();
        bookingThreads.clear();

        for (int i = 0; i < totalThreads; i++) {
            BookingThread bookingThread = new BookingThread(bookingSystem, i);
            Thread thread = new Thread(bookingThread, "BookingThread-" + i);

            bookingThreads.add(bookingThread);
            threads.add(thread);
        }
    }

    public void startAllThreads() throws InterruptedException {
        if (isRunning) {
            throw new IllegalStateException("Simulation already running!");
        }

        isRunning = true;

        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void waitForCompletion() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
        isRunning = false;
    }

    public void stopAllThreads() {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        isRunning = false;
    }

    public void runSimulation() throws InterruptedException {
        notifySimulationStarted(SimulationConfig.TOTAL_USERS, bookingSystem.isSafeMode());

        prepareThreads(SimulationConfig.TOTAL_USERS);

        Thread progressMonitor = new Thread(() -> {
            try {
                while (isRunning) {
                    int active = getActiveThreadCount();
                    int completed = SimulationConfig.TOTAL_USERS - active;
                    notifyProgressUpdate(active, completed, SimulationConfig.TOTAL_USERS);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        progressMonitor.start();

        try {
            startAllThreads();
            waitForCompletion();
        } finally {
            progressMonitor.interrupt();
            progressMonitor.join();
        }

        notifyProgressUpdate(0, SimulationConfig.TOTAL_USERS, SimulationConfig.TOTAL_USERS);

        bookingSystem.calculateFinalStats();

        notifySimulationCompleted(bookingSystem.getStats());
    }

    public boolean isRunning() {
        return isRunning;
    }

    public List<BookingThread> getBookingThreads() {
        return new ArrayList<>(bookingThreads);
    }

    public int getActiveThreadCount() {
        return (int) threads.stream()
                .filter(Thread::isAlive)
                .count();
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    private void notifySimulationStarted(int totalThreads, boolean isSafeMode) {
        observers.forEach(observer -> observer.onSimulationStarted(totalThreads, isSafeMode));
    }

    private void notifyProgressUpdate(int activeThreads, int completedThreads, int totalThreads) {
        observers.forEach(observer -> observer.onProgressUpdate(activeThreads, completedThreads, totalThreads));
    }

    private void notifySimulationCompleted(SimulationStats stats) {
        observers.forEach(observer -> observer.onSimulationCompleted(stats));
    }
}