package com.booking.console;

import com.booking.model.SimulationStats;

public class ConsoleFormatter {

    public void printSimulationHeader(String mode) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Running " + mode + " MODE Simulation");
        System.out.println("=".repeat(60));
        System.out.println("Seats: 100 | Threads: 1000 | Mode: " + mode);
        System.out.println();
    }

    public void printSimulationComplete(long timeMs) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  SIMULATION COMPLETE");
        System.out.println("=".repeat(60));
        System.out.println("Time taken: " + timeMs + "ms");
        System.out.println();
    }

    public void printResults(SimulationStats stats) {
        System.out.println("STATISTICS:");
        System.out.println("-".repeat(40));
        System.out.printf("  Total Seats:           %4d%n", stats.getTotalSeats());
        System.out.printf("  Total Threads:         %4d%n", stats.getTotalUsers());
        System.out.printf("  Seats Booked:          %4d%n", stats.getSeatsBooked());
        System.out.printf("  Successful Bookings:   %4d%n", stats.getSuccessfulBookings());
        System.out.printf("  Collisions:            %4d%n", stats.getCollisions());
        System.out.printf("  Oversold By:           %4d%n", stats.getOversoldCount());
        System.out.println("-".repeat(40));

        System.out.println("\nVERDICT:");
        System.out.println(stats.getVerdict());
    }

    public void printComparisonHeader() {
        System.out.println("\n" + "=".repeat(62));
        System.out.println("RUNNING COMPARISON: SAFE vs UNSAFE");
        System.out.println("=".repeat(62));
    }

    public void printComparisonResults(SimulationStats safe, SimulationStats unsafe) {
        System.out.println("\n" + "=".repeat(62));
        System.out.println("COMPARISON RESULTS");
        System.out.println("=".repeat(62));

        System.out.printf("%-30s %15s %15s%n", "Metric", "SAFE MODE", "UNSAFE MODE");
        System.out.println("-".repeat(62));
        System.out.printf("%-30s %15d %15d%n", "Seats Booked",
                safe.getSeatsBooked(), unsafe.getSeatsBooked());
        System.out.printf("%-30s %15d %15d%n", "Successful Bookings",
                safe.getSuccessfulBookings(), unsafe.getSuccessfulBookings());
        System.out.printf("%-30s %15d %15d%n", "Collisions",
                safe.getCollisions(), unsafe.getCollisions());
        System.out.printf("%-30s %15d %15d%n", "Oversold",
                safe.getOversoldCount(), unsafe.getOversoldCount());
        System.out.println("-".repeat(62));

        System.out.println("\nSafe Mode: Perfect integrity - no race conditions");
        System.out.println("Unsafe Mode: System compromised - " +
                unsafe.getCollisions() + " collisions detected");
    }
}