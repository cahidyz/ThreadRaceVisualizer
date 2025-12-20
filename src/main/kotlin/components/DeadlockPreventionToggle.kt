package components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.AppColors

@Composable
fun DeadlockPreventionToggle(
    useConsistentLockOrder: Boolean,
    onToggle: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Use Consistent Lock Order",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (useConsistentLockOrder) {
                        "Deadlock prevention enabled (fixed order prevents circular wait)"
                    } else {
                        "Deadlock prevention disabled (random lock order creates circular wait)"
                    },
                    fontSize = 12.sp,
                    color = if (useConsistentLockOrder) AppColors.Safe else AppColors.TextSecondary
                )
            }

            Checkbox(
                checked = useConsistentLockOrder,
                onCheckedChange = onToggle,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.TextSecondary,
                    uncheckedColor = AppColors.TextSecondary,
                    checkmarkColor = AppColors.TextOnPrimary,
                    disabledColor = AppColors.TextSecondary
                )
            )
        }
    }
}
