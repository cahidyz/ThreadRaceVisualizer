package com.booking.console;

import java.util.Scanner;

public class Main {

    private static final SimulationRunner runner = new SimulationRunner();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runner.runSafeMode();
                    break;
                case "2":
                    runner.runUnsafeMode();
                    break;
                case "3":
                    runner.runComparison();
                    break;
                case "4":
                    System.out.println("\nGoodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(51));
        System.out.println("THREAD RACE VISUALIZER - CONSOLE DEMO");
        System.out.println("=".repeat(51));
        System.out.println("1. Run SAFE MODE (synchronized - no collisions)");
        System.out.println("2. Run UNSAFE MODE (race conditions - collisions)");
        System.out.println("3. Run BOTH and COMPARE");
        System.out.println("4. Exit");
        System.out.println("=".repeat(51));
        System.out.print("Choose option: ");
    }
}