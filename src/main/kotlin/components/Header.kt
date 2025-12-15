package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

enum class SimulationStatus {
    IDLE, RUNNING, COMPLETED, STOPPED
}

enum class SimulationMode {
    SAFE, UNSAFE
}

@Composable
fun Header(
    status: SimulationStatus,
    mode: SimulationMode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title
        Text(
            text = "Thread Race Visualizer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        
        // Legend and Status
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Legend
            LegendSection()
            
            // Status indicator
            StatusIndicator(status)
            
            // Mode indicator
            ModeIndicator(mode)
        }
    }
}

@Composable
private fun LegendSection() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = AppColors.SeatEmpty, borderColor = AppColors.SeatEmptyBorder, label = "Empty")
        LegendItem(color = AppColors.SeatBooked, borderColor = AppColors.SeatBookedBorder, label = "Booked")
        LegendItem(color = AppColors.SeatCollision, borderColor = AppColors.SeatCollisionBorder, label = "Collision")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    borderColor: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
private fun StatusIndicator(status: SimulationStatus) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Status:",
            fontSize = 14.sp,
            color = AppColors.TextSecondary
        )
        
        val statusColor = when (status) {
            SimulationStatus.IDLE -> AppColors.TextSecondary
            SimulationStatus.RUNNING -> AppColors.Safe
            SimulationStatus.COMPLETED -> AppColors.Safe
            SimulationStatus.STOPPED -> AppColors.Unsafe
        }
        
        Text(
            text = status.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
private fun ModeIndicator(mode: SimulationMode) {
    val backgroundColor = when (mode) {
        SimulationMode.SAFE -> AppColors.ModeSafe
        SimulationMode.UNSAFE -> AppColors.ModeUnsafe
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Mode: ${mode.name}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextOnPrimary
        )
    }
}
