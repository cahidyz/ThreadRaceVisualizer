package com.booking.strategy;

public class BookingStrategyFactory {

    public static BookingStrategy create(StrategyType strategyType) {
        return switch (strategyType) {
            case SYNCHRONIZED -> new SynchronizedBookingStrategy();
            case RACE_CONDITION -> new RaceConditionSimulationStrategy();
        };
    }
}