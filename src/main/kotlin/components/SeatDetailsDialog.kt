package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import theme.AppColors

@Composable
fun SeatDetailsDialog(
    seat: Seat,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp,
            backgroundColor = AppColors.Surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with seat number and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seat #${seat.id}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    // Status badge
                    StatusBadge(state = seat.state)
                }

                Divider(color = AppColors.StatCardBorder)

                // Booking information
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status description
                    val statusText = when (seat.state) {
                        SeatState.EMPTY -> "This seat has not been booked yet."
                        SeatState.BOOKED -> "This seat was successfully booked by a single thread."
                        SeatState.COLLISION -> "RACE CONDITION DETECTED! Multiple threads attempted to book this seat."
                        SeatState.DEADLOCKED -> "DEADLOCK DETECTED! This seat is locked in a circular wait with its paired popcorn."
                    }

                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        color = when (seat.state) {
                            SeatState.COLLISION -> AppColors.TextPrimary
                            SeatState.DEADLOCKED -> AppColors.SeatDeadlocked
                            else -> AppColors.TextSecondary
                        }
                    )

                    // Thread IDs section
                    if (seat.threadIds.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (seat.state == SeatState.COLLISION)
                                "Competing Threads (${seat.threadIds.size}):"
                            else "Booked by Thread:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary
                        )

                        // Thread ID list
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 150.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.Background)
                                .border(1.dp, AppColors.StatCardBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            if (seat.threadIds.size <= 5) {
                                // Show all threads if 5 or fewer
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    seat.threadIds.forEachIndexed { index, threadId ->
                                        ThreadIdRow(
                                            threadId = threadId,
                                            isFirst = index == 0,
                                            isCollision = seat.state == SeatState.COLLISION,
                                            isDeadlock = seat.state == SeatState.DEADLOCKED
                                        )
                                    }
                                }
                            } else {
                                // Use scrollable list for many threads
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(seat.threadIds.toList()) { threadId ->
                                        ThreadIdRow(
                                            threadId = threadId,
                                            isFirst = threadId == seat.threadIds.first(),
                                            isCollision = seat.state == SeatState.COLLISION,
                                            isDeadlock = seat.state == SeatState.DEADLOCKED
                                        )
                                    }
                                }
                            }
                        }
                    } else if (seat.state == SeatState.EMPTY) {
                        Text(
                            text = "No booking attempts recorded.",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = when (seat.state) {
                            SeatState.EMPTY -> AppColors.TextSecondary
                            SeatState.BOOKED -> AppColors.SeatBooked
                            SeatState.COLLISION -> AppColors.SeatCollision
                            SeatState.DEADLOCKED -> AppColors.SeatDeadlocked
                        },
                        contentColor = AppColors.TextOnPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(state: SeatState) {
    val (backgroundColor, text) = when (state) {
        SeatState.EMPTY -> Pair(AppColors.SeatEmpty, "EMPTY")
        SeatState.BOOKED -> Pair(AppColors.SeatBooked, "BOOKED")
        SeatState.COLLISION -> Pair(AppColors.SeatCollision, "COLLISION")
        SeatState.DEADLOCKED -> Pair(AppColors.SeatDeadlocked, "DEADLOCKED")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (state == SeatState.EMPTY) AppColors.TextSecondary else AppColors.TextOnPrimary
        )
    }
}

@Composable
private fun ThreadIdRow(
    threadId: Int,
    isFirst: Boolean,
    isCollision: Boolean,
    isDeadlock: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thread icon
            Text(
                text = "◉",
                fontSize = 10.sp,
                color = when {
                    isCollision -> AppColors.SeatCollision
                    isDeadlock -> AppColors.SeatDeadlocked
                    else -> AppColors.SeatBooked
                }
            )

            Text(
                text = "Thread #$threadId",
                fontSize = 13.sp,
                color = AppColors.TextPrimary
            )
        }

        // Label for first thread or collision indicator
        if (isFirst && !isCollision && !isDeadlock) {
            Text(
                text = "✓ Winner",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.SeatBooked
            )
        } else if (isCollision) {
            Text(
                text = "⚠ Competed",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.SeatCollision
            )
        }
    }
}
