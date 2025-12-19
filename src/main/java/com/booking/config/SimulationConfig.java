package com.booking.config;

public class SimulationConfig {

    // Grid configuration
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLUMNS = 10;
    public static final int TOTAL_SEATS = GRID_ROWS * GRID_COLUMNS;
    public static final int TOTAL_USERS = 1000;
    public static int UNSAFE_BOOKING_DELAY = 2;

    // Deadlock configuration
    public static final int TOTAL_POPCORNS = TOTAL_SEATS;
    public static final long DEADLOCK_TIMEOUT_MS = 5000;
    public static int LOCK_ACQUISITION_DELAY_MS = 5;

    private SimulationConfig() {
    }
}