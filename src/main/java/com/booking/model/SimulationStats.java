package com.booking.model;

public class SimulationStats {
    private final int totalSeats;
    private final int totalUsers;
    private int seatsBooked;
    private int successfulBookings;
    private int collisions;
    private int oversoldCount;
    private boolean isSafeMode;
    private int pairsComplete;
    private int deadlocksDetected;
    private int threadsStuck;
    private int popcornsReserved;
    private boolean isDeadlockMode;

    public SimulationStats(int totalSeats, int totalUsers) {
        this.totalSeats = totalSeats;
        this.totalUsers = totalUsers;
        this.seatsBooked = 0;
        this.successfulBookings = 0;
        this.collisions = 0;
        this.isSafeMode = true;
        this.pairsComplete = 0;
        this.deadlocksDetected = 0;
        this.threadsStuck = 0;
        this.popcornsReserved = 0;
        this.isDeadlockMode = false;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public int getSuccessfulBookings() {
        return successfulBookings;
    }

    public void setSafeMode(boolean safeMode) {
        isSafeMode = safeMode;
    }

    public void setSuccessfulBookings(int successfulBookings) {
        this.successfulBookings = successfulBookings;
    }

    public int getCollisions() {
        return collisions;
    }

    public void setCollisions(int collisions) {
        this.collisions = collisions;
    }

    public int getOversoldCount() {
        return oversoldCount;
    }

    public void setOversoldCount(int oversoldCount) {
        this.oversoldCount = oversoldCount;
    }

    public int getPairsComplete() {
        return pairsComplete;
    }

    public void setPairsComplete(int pairsComplete) {
        this.pairsComplete = pairsComplete;
    }

    public int getDeadlocksDetected() {
        return deadlocksDetected;
    }

    public void setDeadlocksDetected(int deadlocksDetected) {
        this.deadlocksDetected = deadlocksDetected;
    }

    public int getThreadsStuck() {
        return threadsStuck;
    }

    public void setThreadsStuck(int threadsStuck) {
        this.threadsStuck = threadsStuck;
    }

    public int getPopcornsReserved() {
        return popcornsReserved;
    }

    public void setPopcornsReserved(int popcornsReserved) {
        this.popcornsReserved = popcornsReserved;
    }

    public boolean isDeadlockMode() {
        return isDeadlockMode;
    }

    public void setDeadlockMode(boolean deadlockMode) {
        isDeadlockMode = deadlockMode;
    }

    public String getVerdict() {
        if (isDeadlockMode) {
            return "DEADLOCK MODE\n" +
                    "Pairs Complete: " + pairsComplete + "\n" +
                    "Deadlocks Detected: " + deadlocksDetected + "\n" +
                    "Threads Stuck: " + threadsStuck + "\n" +
                    "System integrity: DEADLOCKED";
        } else if (isSafeMode) {
            return "SAFE MODE\n" +
                    seatsBooked + " seats booked safely.\n" +
                    "System integrity: INTACT";
        } else {
            return "UNSAFE MODE\n" +
                    "Actual Seats: " + seatsBooked + "\n" +
                    "Claimed Bookings: " + successfulBookings + "\n" +
                    "Collisions: " + collisions + "\n" +
                    "System integrity: COMPROMISED";
        }
    }

    public String getSummary() {
        if (isDeadlockMode) {
            return String.format(
                    "[DEADLOCK] Pairs: %d/%d | Deadlocks: %d | Stuck: %d",
                    pairsComplete, totalSeats, deadlocksDetected, threadsStuck
            );
        } else if (isSafeMode) {
            return String.format("[SAFE]   Seats: %d/%d (Perfect)", seatsBooked, totalSeats);
        }

        return String.format(
                "[UNSAFE] Seats: %d/%d | Claims: %d | Collisions: %d",
                seatsBooked, totalSeats, successfulBookings, collisions
        );
    }

    public void reset() {
        this.seatsBooked = 0;
        this.successfulBookings = 0;
        this.collisions = 0;
        this.oversoldCount = 0;
        this.pairsComplete = 0;
        this.deadlocksDetected = 0;
        this.threadsStuck = 0;
        this.popcornsReserved = 0;
    }
}
