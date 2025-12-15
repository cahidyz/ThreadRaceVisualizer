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

        java.util.stream.IntStream.range(0, totalThreads).forEach(i -> {
            BookingThread bookingThread = new BookingThread(bookingSystem, i);
            Thread thread = new Thread(bookingThread, "BookingThread-" + i);

            bookingThreads.add(bookingThread);
            threads.add(thread);
        });
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
        threads.stream()
                .filter(Thread::isAlive)
                .forEach(Thread::interrupt);
        isRunning = false;
    }

    public void runSimulation() throws InterruptedException {
        notifyObservers(observer ->
                observer.onSimulationStarted(SimulationConfig.TOTAL_USERS, bookingSystem.isSafeMode())
        );

        prepareThreads(SimulationConfig.TOTAL_USERS);

        Thread progressMonitor = new Thread(() -> {
            try {
                while (isRunning) {
                    int active = getActiveThreadCount();
                    int completed = SimulationConfig.TOTAL_USERS - active;

                    notifyObservers(observer ->
                            observer.onProgressUpdate(active, completed, SimulationConfig.TOTAL_USERS)
                    );

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        progressMonitor.start();

        startAllThreads();
        waitForCompletion();

        progressMonitor.interrupt();
        progressMonitor.join();

        notifyObservers(observer ->
                observer.onProgressUpdate(0, SimulationConfig.TOTAL_USERS, SimulationConfig.TOTAL_USERS)
        );

        bookingSystem.calculateFinalStats();

        notifyObservers(observer ->
                observer.onSimulationCompleted(bookingSystem.getStats())
        );
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
        observers.forEach(observer -> {
            try {
                notification.accept(observer);
            } catch (Exception e) {
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        });
    }

    public void setupBookingSystemObserver() {
        bookingSystem.addObserver(new com.booking.observer.BookingObserver() {
            @Override
            public void onSeatBooked(com.booking.model.Seat seat, int threadId) {
                notifyObservers(observer -> observer.onSeatBooked(seat, threadId));
            }

            @Override
            public void onBookingFailed(int threadId) {
                notifyObservers(observer -> observer.onBookingFailed(threadId));
            }

            @Override
            public void onCollisionDetected(com.booking.model.Seat seat) {
                notifyObservers(observer -> observer.onCollisionDetected(seat));
            }

            @Override
            public void onThreadStarted(int threadId) {
                notifyObservers(observer -> observer.onThreadStarted(threadId));
            }

            @Override
            public void onThreadCompleted(int threadId) {
                notifyObservers(observer -> observer.onThreadCompleted(threadId));
            }

            @Override
            public void onSimulationStarted(int totalThreads, boolean isSafeMode) {
            }

            @Override
            public void onSimulationCompleted(com.booking.model.SimulationStats stats) {
            }

            @Override
            public void onProgressUpdate(int activeThreads, int completedThreads, int totalThreads) {
            }
        });
    }
}