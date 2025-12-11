package com.booking.config;

public class SimulationConfig {

    //SCREAMING_SNAKE_CASES
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLUMNS = 10;
    public static final int TOTAL_SEATS = GRID_ROWS * GRID_COLUMNS;
    public static final int TOTAL_USERS = 1000;
    public static volatile long THREAD_DELAY = 0;
    public static final int UNSAFE_BOOKING_DELAY = 2;
    public static volatile boolean DEBUG_MODE = false;

    public static void reset() {
        THREAD_DELAY = 0;
        DEBUG_MODE = false;
    }
}