package com.booking.model;

public class SimulationStats {
    private final int totalSeats;
    private final int totalUsers;
    private int seatsBooked;
    private int successfulBookings;
    private int collisions;
    private boolean isSafeMode;

    public SimulationStats(int totalSeats, int totalUsers) {
        this.totalSeats = totalSeats;
        this.totalUsers = totalUsers;
        this.seatsBooked = 0;
        this.successfulBookings = 0;
        this.collisions = 0;
        this.isSafeMode = true;
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
        if (successfulBookings > seatsBooked) {
            return successfulBookings - seatsBooked;
        }
        return 0;
    }

    // maybe I will delete this one
    public double getOversellPercentage() {
        if (successfulBookings == 0) return 0.0;
        return (getOversoldCount() * 100.0) / successfulBookings;
    }

    public String getVerdict() {
        if (isSafeMode) {
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
        if (isSafeMode) {
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
    }
}
