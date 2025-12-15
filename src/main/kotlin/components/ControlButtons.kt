package components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

@Composable
fun ControlButtons(
    mode: SimulationMode,
    status: SimulationStatus,
    onModeChange: (SimulationMode) -> Unit,
    onRunSimulation: () -> Unit,
    onReset: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = status == SimulationStatus.RUNNING
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode toggle buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // SAFE button
            ModeButton(
                text = "SAFE",
                isSelected = mode == SimulationMode.SAFE,
                onClick = { onModeChange(SimulationMode.SAFE) },
                enabled = !isRunning,
                isLeft = true
            )
            
            // UNSAFE button
            ModeButton(
                text = "UNSAFE",
                isSelected = mode == SimulationMode.UNSAFE,
                onClick = { onModeChange(SimulationMode.UNSAFE) },
                enabled = !isRunning,
                isLeft = false
            )
        }
        
        // Run Simulation button
        Button(
            onClick = onRunSimulation,
            enabled = !isRunning,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColors.ButtonRun,
                contentColor = AppColors.TextOnPrimary,
                disabledBackgroundColor = AppColors.ButtonRun.copy(alpha = 0.5f),
                disabledContentColor = AppColors.TextOnPrimary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "▶",
                    fontSize = 14.sp
                )
                Text(
                    text = "Run Simulation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Reset button
        Button(
            onClick = onReset,
            enabled = status != SimulationStatus.RUNNING,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColors.ButtonReset,
                contentColor = AppColors.TextOnPrimary,
                disabledBackgroundColor = AppColors.ButtonReset.copy(alpha = 0.5f),
                disabledContentColor = AppColors.TextOnPrimary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(0.8f)
                .height(44.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↺",
                    fontSize = 16.sp
                )
                Text(
                    text = "Reset",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Stop button
        OutlinedButton(
            onClick = onStop,
            enabled = isRunning,
            border = BorderStroke(
                1.dp,
                if (isRunning) AppColors.TextSecondary else AppColors.ButtonStop
            ),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = Color.Transparent,
                contentColor = if (isRunning) AppColors.TextSecondary else AppColors.ButtonStopText,
                disabledContentColor = AppColors.ButtonStopText
            ),
            modifier = Modifier
                .weight(0.6f)
                .height(44.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "■",
                    fontSize = 12.sp
                )
                Text(
                    text = "Stop",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    isLeft: Boolean
) {
    val shape = when {
        isLeft -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        else -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    }
    
    if (isSelected) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColors.ButtonSafe,
                contentColor = AppColors.ButtonSafeText,
                disabledBackgroundColor = AppColors.ButtonSafe.copy(alpha = 0.7f),
                disabledContentColor = AppColors.ButtonSafeText.copy(alpha = 0.7f)
            ),
            shape = shape,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            modifier = Modifier.height(44.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            border = BorderStroke(1.dp, AppColors.ButtonUnsafeBorder),
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = AppColors.ButtonUnsafe,
                contentColor = AppColors.ButtonUnsafeText,
                disabledContentColor = AppColors.ButtonUnsafeText.copy(alpha = 0.5f)
            ),
            modifier = Modifier.height(44.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
