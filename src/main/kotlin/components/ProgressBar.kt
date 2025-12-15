package components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

@Composable
fun ProgressBar(
    currentProgress: Int,
    totalThreads: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalThreads > 0) {
        (currentProgress.toFloat() / totalThreads.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100)
    )
    
    val percentage = (animatedProgress * 100).toInt()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.ProgressBackground)
    ) {
        // Progress fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppColors.ProgressFill,
                            AppColors.ProgressFill.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        
        // Progress text
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Progress: $currentProgress/$totalThreads threads ($percentage%)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.ProgressText
            )
        }
    }
}
