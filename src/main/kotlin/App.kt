package app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import components.*
import kotlinx.coroutines.*
import theme.AppColors

// Import Java backend classes
import com.booking.config.SimulationConfig
import com.booking.core.BookingSystem
import com.booking.observer.BookingObserver
import com.booking.strategy.BookingStrategyFactory
import com.booking.strategy.StrategyType
import com.booking.thread.ThreadManager
import com.booking.model.Seat as JavaSeat
import com.booking.model.Popcorn as JavaPopcorn
import com.booking.model.SimulationStats as JavaSimulationStats

class SimulationState {
    var mode by mutableStateOf(SimulationMode.SAFE)
    var status by mutableStateOf(SimulationStatus.IDLE)
    var delayMs by mutableStateOf(2)
    var seats by mutableStateOf(createInitialSeats())
    var stats by mutableStateOf(SimulationStats())
    var progress by mutableStateOf(0)
    var useConsistentLockOrder by mutableStateOf(false)

    private var simulationJob: Job? = null
    private var bookingSystem: BookingSystem? = null
    private var threadManager: ThreadManager? = null

    fun reset() {
        simulationJob?.cancel()
        threadManager?.stopAllThreads()
        status = SimulationStatus.IDLE
        seats = createInitialSeats()
        stats = SimulationStats()
        progress = 0
        bookingSystem = null
        threadManager = null
    }

    fun stop() {
        simulationJob?.cancel()
        threadManager?.stopAllThreads()
        status = SimulationStatus.STOPPED
    }

    fun runSimulation(scope: CoroutineScope) {
        if (status == SimulationStatus.RUNNING) return

        // Reset state
        seats = createInitialSeats()
        stats = SimulationStats()
        progress = 0
        status = SimulationStatus.RUNNING

        // Update the Java config with current delay setting
        SimulationConfig.UNSAFE_BOOKING_DELAY = delayMs

        // Create Java backend components
        val isSafeMode = mode == SimulationMode.SAFE
        val isDeadlockMode = mode == SimulationMode.DEADLOCK
        val strategyType = when (mode) {
            SimulationMode.SAFE -> StrategyType.SYNCHRONIZED
            SimulationMode.UNSAFE -> StrategyType.RACE_CONDITION
            SimulationMode.DEADLOCK -> if (useConsistentLockOrder) StrategyType.DEADLOCK_PREVENTION else StrategyType.DEADLOCK
        }
        val strategy = BookingStrategyFactory.create(strategyType)
        bookingSystem = BookingSystem(
            SimulationConfig.TOTAL_SEATS,
            SimulationConfig.TOTAL_USERS,
            isSafeMode,
            strategy,
            isDeadlockMode
        )
        threadManager = ThreadManager(bookingSystem)

        // Setup observer to receive events from Java backend
        val observer = createObserver(scope)
        bookingSystem!!.addObserver(observer)
        threadManager!!.addObserver(observer)

        // Run simulation in background thread
        simulationJob = scope.launch(Dispatchers.IO) {
            try {
                threadManager!!.runSimulation()
            } catch (e: InterruptedException) {
                // Simulation was stopped
                withContext(Dispatchers.Main) {
                    status = SimulationStatus.STOPPED
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    status = SimulationStatus.STOPPED
                }
            }
        }
    }

    private fun createObserver(scope: CoroutineScope): BookingObserver {
        return object : BookingObserver {
            override fun onSimulationStarted(totalThreads: Int, isSafeMode: Boolean) {
                scope.launch(Dispatchers.Main) {
                    progress = 0
                    status = SimulationStatus.RUNNING
                }
            }

            override fun onSeatBooked(seat: JavaSeat, threadId: Int) {
                scope.launch(Dispatchers.Main) {
                    updateSeatFromJava(seat)
                }
            }

            override fun onCollisionDetected(seat: JavaSeat) {
                scope.launch(Dispatchers.Main) {
                    updateSeatFromJava(seat)
                }
            }

            override fun onBookingFailed(threadId: Int) {
                // No action needed - seat was already booked
            }

            override fun onThreadStarted(threadId: Int) {
                // Optional: could track active threads
            }

            override fun onThreadCompleted(threadId: Int) {
                // Progress is updated via onProgressUpdate
            }

            override fun onProgressUpdate(activeThreads: Int, completedThreads: Int, totalThreads: Int) {
                scope.launch(Dispatchers.Main) {
                    progress = completedThreads
                }
            }

            override fun onSimulationCompleted(stats: JavaSimulationStats) {
                scope.launch(Dispatchers.Main) {
                    // Update final stats from Java backend
                    if (stats.isDeadlockMode) {
                        this@SimulationState.stats = this@SimulationState.stats.copy(
                            seatsBooked = stats.seatsBooked,
                            pairsComplete = stats.pairsComplete,
                            deadlocksDetected = stats.deadlocksDetected,
                            threadsStuck = stats.threadsStuck,
                            popcornsReserved = stats.popcornsReserved
                        )
                    } else {
                        this@SimulationState.stats = this@SimulationState.stats.copy(
                            seatsBooked = stats.seatsBooked,
                            successfulBookings = stats.successfulBookings,
                            collisions = stats.collisions,
                            oversoldBy = stats.oversoldCount
                        )
                    }
                    progress = SimulationConfig.TOTAL_USERS
                    status = SimulationStatus.COMPLETED

                    // Final sync of all seat states
                    syncAllSeatsFromJava()
                }
            }

            // Deadlock-specific observer methods
            override fun onDeadlockDetected(seat: JavaSeat, popcorn: JavaPopcorn, threadId: Int) {
                scope.launch(Dispatchers.Main) {
                    updateSeatFromJava(seat)
                    updateStatsFromSeats()
                }
            }

            override fun onPairComplete(seat: JavaSeat, popcorn: JavaPopcorn, threadId: Int) {
                scope.launch(Dispatchers.Main) {
                    updateSeatFromJava(seat)
                    updateStatsFromSeats()
                }
            }

            override fun onThreadStuck(threadId: Int, waitTimeMs: Long) {
                // Stats are updated via onDeadlockDetected
            }
        }
    }

    private fun updateSeatFromJava(javaSeat: JavaSeat) {
        val seatId = javaSeat.seatNumber
        val newState = when {
            !javaSeat.isBooked() && !javaSeat.isDeadlocked -> SeatState.EMPTY
            javaSeat.isDeadlocked -> SeatState.DEADLOCKED
            javaSeat.isHasCollision -> SeatState.COLLISION
            else -> SeatState.BOOKED
        }
        // Make defensive copy to avoid ConcurrentModificationException
        val threadIds = javaSeat.threadIds?.let { ArrayList(it) }?.filterNotNull()?.map { it.toInt() } ?: emptyList()
        val bookingCount = threadIds.size

        seats = seats.toMutableList().apply {
            if (seatId in indices) {
                this[seatId] = this[seatId].copy(
                    state = newState,
                    bookingCount = bookingCount,
                    threadIds = threadIds
                )
            }
        }

        // Update running stats
        updateStatsFromSeats()
    }

    private fun syncAllSeatsFromJava() {
        val javaSeats = bookingSystem?.seats ?: return
        seats = seats.toMutableList().apply {
            for (javaSeat in javaSeats) {
                val seatId = javaSeat.seatNumber
                val newState = when {
                    !javaSeat.isBooked() && !javaSeat.isDeadlocked -> SeatState.EMPTY
                    javaSeat.isDeadlocked -> SeatState.DEADLOCKED
                    javaSeat.isHasCollision -> SeatState.COLLISION
                    else -> SeatState.BOOKED
                }
                // Make defensive copy to avoid ConcurrentModificationException
                val threadIds = javaSeat.threadIds?.let { ArrayList(it) }?.filterNotNull()?.map { it.toInt() } ?: emptyList()
                val bookingCount = threadIds.size
                if (seatId in indices) {
                    this[seatId] = this[seatId].copy(
                        state = newState,
                        bookingCount = bookingCount,
                        threadIds = threadIds
                    )
                }
            }
        }
    }

    private fun updateStatsFromSeats() {
        if (mode == SimulationMode.DEADLOCK) {
            // Deadlock mode stats
            val pairsComplete = seats.count { it.state == SeatState.BOOKED }
            val deadlocks = seats.count { it.state == SeatState.DEADLOCKED }
            val threadsStuck = seats.filter { it.state == SeatState.DEADLOCKED }.sumOf { it.bookingCount }
            val popcornsReserved = bookingSystem?.popcorns?.count { it.isReserved } ?: 0

            stats = stats.copy(
                seatsBooked = seats.count { it.state != SeatState.EMPTY },
                pairsComplete = pairsComplete,
                deadlocksDetected = deadlocks,
                threadsStuck = threadsStuck,
                popcornsReserved = popcornsReserved
            )
        } else {
            // Normal mode stats
            val bookedSeats = seats.count { it.state != SeatState.EMPTY }
            val successfulBookings = seats.count { it.state == SeatState.BOOKED }
            val collisions = seats.count { it.state == SeatState.COLLISION }
            val totalAttempts = seats.sumOf { it.bookingCount }
            val oversold = (totalAttempts - 100).coerceAtLeast(0)

            stats = stats.copy(
                seatsBooked = bookedSeats,
                successfulBookings = successfulBookings,
                collisions = collisions,
                oversoldBy = oversold
            )
        }
    }
}

@Composable
fun App() {
    val state = remember { SimulationState() }
    val scope = rememberCoroutineScope()

    MaterialTheme(
        colors = lightColors(
            primary = AppColors.Primary,
            primaryVariant = AppColors.PrimaryDark,
            secondary = AppColors.Unsafe,
            background = AppColors.Background,
            surface = AppColors.Surface
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppColors.Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Header(
                    status = state.status,
                    mode = state.mode
                )

                // Main content area
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Seat grid (left side) - White card with rounded corners
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.Surface,
                        elevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SeatGrid(seats = state.seats)
                        }
                    }

                    // Statistics panel (right side)
                    StatisticsPanel(
                        stats = state.stats,
                        mode = state.mode
                    )
                }

                // Progress bar
                ProgressBar(
                    currentProgress = state.progress,
                    totalThreads = state.stats.totalThreads
                )

                // Mode-aware control area
                if (state.mode == SimulationMode.DEADLOCK) {
                    // Deadlock Prevention Toggle
                    DeadlockPreventionToggle(
                        useConsistentLockOrder = state.useConsistentLockOrder,
                        onToggle = { state.useConsistentLockOrder = it },
                        enabled = state.status != SimulationStatus.RUNNING
                    )
                } else {
                    // Race Condition Delay Slider (SAFE/UNSAFE modes)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp))
                            .background(AppColors.Surface, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        DelaySlider(
                            delayMs = state.delayMs,
                            onDelayChange = { state.delayMs = it },
                            enabled = state.status != SimulationStatus.RUNNING,
                            mode = state.mode
                        )
                    }
                }

                // Control buttons
                ControlButtons(
                    mode = state.mode,
                    status = state.status,
                    onModeChange = { state.mode = it },
                    onRunSimulation = {
                        state.runSimulation(scope)
                    },
                    onReset = { state.reset() },
                    onStop = { state.stop() }
                )
            }
        }
    }
}
