package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

@Composable
fun DelaySlider(
    delayMs: Int,
    onDelayChange: (Int) -> Unit,
    enabled: Boolean,
    mode: SimulationMode,
    modifier: Modifier = Modifier
) {
    val isUnsafeMode = mode == SimulationMode.UNSAFE
    val sliderEnabled = enabled && isUnsafeMode
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Label and badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Race Condition Slider",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (sliderEnabled) AppColors.TextPrimary else AppColors.TextSecondary
                )
                
                // UNSAFE MODE ONLY badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.BadgeUnsafe)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "UNSAFE MODE ONLY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.BadgeText
                    )
                }
            }
            
            // Right side: Current value
            Text(
                text = "${delayMs}ms",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (sliderEnabled) AppColors.TextPrimary else AppColors.TextSecondary
            )
        }
        
        // Helper text
        Text(
            text = "Drag right to amplify race conditions",
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
        
        // Slider with labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = delayMs.toFloat(),
                onValueChange = { onDelayChange(it.toInt()) },
                valueRange = 0f..10f,
                steps = 9,
                enabled = sliderEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = if (sliderEnabled) AppColors.SliderThumb else AppColors.SliderInactive,
                    activeTrackColor = if (sliderEnabled) AppColors.SliderTrack else AppColors.SliderInactive,
                    inactiveTrackColor = AppColors.SliderInactive,
                    disabledThumbColor = AppColors.SliderInactive,
                    disabledActiveTrackColor = AppColors.SliderInactive,
                    disabledInactiveTrackColor = AppColors.SliderInactive.copy(alpha = 0.5f)
                ),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Tick labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0ms",
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
            Text(
                text = "5ms",
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
            Text(
                text = "10ms",
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}
