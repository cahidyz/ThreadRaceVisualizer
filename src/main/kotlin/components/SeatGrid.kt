package components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import theme.AppColors

enum class SeatState {
    EMPTY, BOOKED, COLLISION
}

data class Seat(
    val id: Int,
    val state: SeatState = SeatState.EMPTY,
    val bookingCount: Int = 0,
    val threadIds: List<Int> = emptyList()
)

@Composable
fun SeatGrid(
    seats: List<Seat>,
    modifier: Modifier = Modifier
) {
    var selectedSeat by remember { mutableStateOf<Seat?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.width(280.dp)
        ) {
            items(seats) { seat ->
                SeatCell(
                    seat = seat,
                    onClick = { selectedSeat = seat }
                )
            }
        }
    }

    // Show dialog when a seat is selected
    selectedSeat?.let { seat ->
        SeatDetailsDialog(
            seat = seat,
            onDismiss = { selectedSeat = null }
        )
    }
}

@Composable
private fun SeatCell(
    seat: Seat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (seat.state) {
            SeatState.EMPTY -> AppColors.SeatEmpty
            SeatState.BOOKED -> AppColors.SeatBooked
            SeatState.COLLISION -> AppColors.SeatCollision
        },
        animationSpec = tween(durationMillis = 300)
    )

    val borderColor by animateColorAsState(
        targetValue = when (seat.state) {
            SeatState.EMPTY -> AppColors.SeatEmptyBorder
            SeatState.BOOKED -> AppColors.SeatBookedBorder
            SeatState.COLLISION -> AppColors.SeatCollisionBorder
        },
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Seat is clickable - details shown in dialog
    }
}

// Helper function to create initial seats
fun createInitialSeats(count: Int = 100): List<Seat> {
    return (0 until count).map { Seat(id = it) }
}
