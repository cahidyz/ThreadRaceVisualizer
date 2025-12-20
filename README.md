### ConcurrencyVisualizer

ConcurrencyVisualizer is an interactive educational tool designed to visualize complex concurrent programming concepts in real-time. Built with a high-performance Java backend and a Compose Desktop interface, it demonstrates the critical differences between thread-safe operations, race conditions, and deadlock scenarios through a high-concurrency simulation.

#### 🔍 Overview

Concurrency is one of the most challenging aspects of software engineering. ConcurrencyVisualizer bridges the gap between theory and practice by providing a sandbox where 1,000 competing threads attempt to access resources simultaneously. It provides immediate feedback on the success or failure of various synchronization strategies.

* Type: High-Concurrency Desktop Application
* Scale: 100 resources vs. 1,000 competing threads.
* Visuals: Real-time state-driven grid and live telemetry.

#### ✨ Key Features

* Interactive Grid: 10x10 resource matrix showing real-time state changes.

* Live Telemetry: Statistics panel tracking collisions, success rates, and thread completion.

* Deep Inspection: Ability to click resources and view the specific history of competing threads.

* Adaptive Controls: Real-time adjustment of race condition windows (delay slider).

#### 🕹️ Core Logic & Simulation Modes

##### 1. SAFE Mode (Synchronized)

* Uses the synchronized keyword to ensure atomic operations.

* Mechanism: Monitors ensure that only one thread can check and modify resource state at any given moment.

* Outcome: Guarantees 0% collision rate and perfect resource distribution.

##### 2. UNSAFE Mode (Race Condition)

* Simulates a classic "check-then-act" vulnerability with configurable latency.

* Mechanism: Threads read state, enter a variable sleep window (0-10ms), and then attempt to write.

* Outcome: Demonstrates how "stale data" leads to overselling and data corruption.

##### 3. DEADLOCK Mode

* Simulates resource contention between two distinct objects (Seat and Popcorn).

* Chaos Mode: Threads acquire locks in random order, creating a classic circular wait.

* Prevention Mode: Implements a strict resource hierarchy, ensuring all threads acquire locks in a consistent order to eliminate circularity.

#### 🛠️ Technology Stack

##### Logic and Concurrency:

* Java (JVM 17): Core multi-threading logic.

* Synchronization: ReentrantLock and synchronized blocks.

* Collections: CopyOnWriteArrayList and volatile state management.

##### Infrastructure:

* Kotlin 2.2.0: Modern application entry and state flow.

* Kotlinx Coroutines: Non-blocking async operations for real-time monitoring.

* Gradle (Kotlin DSL): Build orchestration.

#### 🏗️ Architecture and Design Patterns

The project follows a decoupled, event-driven architecture to maintain a strict separation between the JVM thread management and the representation layer.

1. Strategy Pattern: Encapsulates various booking algorithms into interchangeable strategies, allowing runtime logic switching.
2. Factory Pattern: Orchestrates the creation of simulation strategies based on user-defined parameters.
3. Observer Pattern: Allows the backend to notify the system of collisions or deadlocks without being aware of the interface layer.

#### 🚀 Technical Execution

When a simulation is triggered, a ThreadManager spawns 1,000 BookingThread instances. These threads interact with the BookingSystem via a selected BookingStrategy. The system manages resource contention and utilizes observers to stream real-time data back to the state manager, ensuring the application remains responsive even under extreme thread pressure.

#### 📥 Installation and Usage

##### Prerequisites

* JDK 17 or higher
* Gradle (wrapper included)

##### Setup and Run

```bash
git clone https://github.com/cahidyz/ConcurrencyVisualizer.git
cd ConcurrencyVisualizer
./gradlew run
```


#### 🎓 Educational Value

ConcurrencyVisualizer is designed as a pedagogical tool for:
* System Architecture: Understanding why ordered resource acquisition prevents deadlocks.

* Software Engineering: Implementing the Strategy pattern for scalable logic.

* Interview Mastery: Real-world visualization of synchronized vs. explicit locking.

#### 📄 Metadata and License

**Author**: Jahid Yusifzada

**Version**: 1.0.0

**License**: Distributed under the MIT License. See LICENSE for more information.