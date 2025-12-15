package com.booking.console;

import com.booking.core.BookingSystem;
import com.booking.strategy.BookingStrategy;
import com.booking.strategy.BookingStrategyFactory;
import com.booking.strategy.StrategyType;
import com.booking.thread.ThreadManager;

public class SimulationRunner {

    private final ConsoleFormatter formatter;

    public SimulationRunner() {
        this.formatter = new ConsoleFormatter();
    }

    public void runSafeMode() {
        runSimulation(true);
    }

    public void runUnsafeMode() {
        runSimulation(false);
    }

    public void runComparison() {
        formatter.printComparisonHeader();

        System.out.println("\nRunning SAFE MODE...");
        BookingStrategy safeStrategy = BookingStrategyFactory.create(StrategyType.SYNCHRONIZED);
        BookingSystem safeSystem = new BookingSystem(100, 1000, true, safeStrategy);
        ThreadManager safeManager = new ThreadManager(safeSystem);

        try {
            safeManager.runSimulation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nRunning UNSAFE MODE...");
        BookingStrategy unsafeStrategy = BookingStrategyFactory.create(StrategyType.RACE_CONDITION);
        BookingSystem unsafeSystem = new BookingSystem(100, 1000, false, unsafeStrategy);
        ThreadManager unsafeManager = new ThreadManager(unsafeSystem);

        try {
            unsafeManager.runSimulation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        formatter.printComparisonResults(safeSystem.getStats(), unsafeSystem.getStats());
    }

    private void runSimulation(boolean safeMode) {
        String mode = safeMode ? "SAFE" : "UNSAFE";

        formatter.printSimulationHeader(mode);

        StrategyType strategyType = safeMode ? StrategyType.SYNCHRONIZED : StrategyType.RACE_CONDITION;
        BookingStrategy strategy = BookingStrategyFactory.create(strategyType);
        BookingSystem system = new BookingSystem(100, 1000, safeMode, strategy);
        ThreadManager manager = new ThreadManager(system);

        try {
            long startTime = System.currentTimeMillis();
            manager.runSimulation();
            long endTime = System.currentTimeMillis();

            formatter.printSimulationComplete(endTime - startTime);
            formatter.printResults(system.getStats());

        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted!");
            Thread.currentThread().interrupt();
        }
    }
}