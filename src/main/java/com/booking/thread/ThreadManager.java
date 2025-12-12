package com.booking.thread;

import com.booking.config.SimulationConfig;
import com.booking.core.BookingSystem;
import com.booking.observer.BookingObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

            long delay = SimulationConfig.THREAD_DELAY;
            if (delay > 0) {
                Thread.sleep(delay);
            }
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
        prepareThreads(SimulationConfig.TOTAL_USERS);
        startAllThreads();
        waitForCompletion();
        bookingSystem.calculateFinalStats();
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