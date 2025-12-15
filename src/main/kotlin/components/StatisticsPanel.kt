package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

data class SimulationStats(
    val totalSeats: Int = 100,
    val totalThreads: Int = 1000,
    val seatsBooked: Int = 0,
    val successfulBookings: Int = 0,
    val collisions: Int = 0,
    val oversoldBy: Int = 0
) {
    val successRate: Double
        get() = if (successfulBookings + collisions > 0) {
            (successfulBookings.toDouble() / (successfulBookings + collisions)) * 100
        } else 0.0
}

@Composable
fun StatisticsPanel(
    stats: SimulationStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Total Seats and Total Threads
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            StatCard(
                icon = Icons.Rounded.GridView,
                iconColor = Color(0xFF2196F3),
                label = "Total Seats",
                value = stats.totalSeats.toString(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 16.dp,
                valueColor = Color.Black
            )
            StatCard(
                icon = Icons.Rounded.Group,
                iconColor = Color(0xFF4CAF50),
                label = "Total Threads",
                value = stats.totalThreads.toString(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 16.dp,
                valueColor = Color.Black
            )
        }

        // Row 2: Seats Booked and Successful Bookings
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            StatCard(
                icon = Icons.Rounded.Bookmark,
                iconColor = Color(0xFFE91E63),
                label = "Seats Booked",
                value = stats.seatsBooked.toString(),
                valueColor = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 16.dp
            )
            StatCard(
                icon = Icons.Rounded.CheckCircle,
                iconColor = Color(0xFF43A047),
                label = "Successful Bookings",
                value = stats.successfulBookings.toString(),
                valueColor = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 16.dp
            )
        }

        // Row 3: Collisions and Oversold
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            StatCard(
                icon = Icons.Rounded.Warning,
                iconColor = Color(0xFFFFC107),
                label = "Collisions",
                value = stats.collisions.toString(),
                valueColor = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 14.dp
            )
            StatCard(
                icon = Icons.Rounded.Error,
                iconColor = Color(0xFFD32F2F),
                label = "Oversold",
                value = stats.oversoldBy.toString(),
                valueColor = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                iconSize = 14.dp
            )
        }

        // Success Rate Card (no weight - keeps natural height)
        SuccessRateCard(successRate = stats.successRate)
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color = AppColors.TextPrimary,
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 18.dp
) {
    Box(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(AppColors.StatCardBackground)
            .border(1.dp, AppColors.StatCardBorder, RoundedCornerShape(6.dp))
            .padding(5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Icon with tinted circular background
            Box(
                modifier = Modifier
                    .size(iconSize + 8.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }

            // Label
            Text(
                text = label,
                fontSize = 8.sp,
                color = AppColors.TextSecondary,
                maxLines = 1
            )

            // Value
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
private fun SuccessRateCard(
    successRate: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(AppColors.StatCardBackground)
            .border(1.dp, AppColors.StatCardBorder, RoundedCornerShape(6.dp))
            .padding(vertical = 8.dp, horizontal = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColor = Color(0xFF2E7D32)

            // Icon with tinted circular background
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                    contentDescription = "Success Rate",
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Success Rate: ${"%.1f".format(successRate)}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = iconColor
            )
        }
    }
}
