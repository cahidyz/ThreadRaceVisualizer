package theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Primary colors
    val Primary = Color(0xFF4CAF50)
    val PrimaryDark = Color(0xFF388E3C)
    val PrimaryLight = Color(0xFF81C784)
    
    // Status colors
    val Safe = Color(0xFF4CAF50)
    val Unsafe = Color(0xFFE91E63)
    
    // Seat states
    val SeatEmpty = Color(0xFFE0E0E0)
    val SeatEmptyBorder = Color(0xFFBDBDBD)
    val SeatBooked = Color(0xFF4CAF50)
    val SeatBookedBorder = Color(0xFF388E3C)
    val SeatCollision = Color(0xFFE91E63)
    val SeatCollisionBorder = Color(0xFFC2185B)
    val SeatDeadlocked = Color(0xFFFF9800)
    val SeatDeadlockedBorder = Color(0xFFE65100)
    
    // UI colors
    val Background = Color(0xFFF5F5F5)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceBorder = Color(0xFFE0E0E0)
    
    // Text colors
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val TextOnPrimary = Color(0xFFFFFFFF)
    
    // Button colors
    val ButtonSafe = Color(0xFF4CAF50)
    val ButtonSafeText = Color(0xFFFFFFFF)
    val ButtonUnsafe = Color(0xFFFFFFFF)
    val ButtonUnsafeBorder = Color(0xFFBDBDBD)
    val ButtonUnsafeText = Color(0xFF616161)
    val ButtonRun = Color(0xFF4CAF50)
    val ButtonReset = Color(0xFF9E9E9E)
    val ButtonStop = Color(0xFFE0E0E0)
    val ButtonStopText = Color(0xFF9E9E9E)
    
    // Progress bar
    val ProgressBackground = Color(0xFF9E9E9E)
    val ProgressFill = Color(0xFF4CAF50)
    val ProgressText = Color(0xFFFFFFFF)
    
    // Statistics panel
    val StatCardBackground = Color(0xFFFFFFFF)
    val StatCardBorder = Color(0xFFE8E8E8)
    
    // Icon colors
    val IconSeats = Color(0xFF2196F3)
    val IconThreads = Color(0xFF4CAF50)
    val IconBooked = Color(0xFFE91E63)
    val IconSuccess = Color(0xFF4CAF50)
    val IconCollision = Color(0xFFFFC107)
    val IconOversold = Color(0xFFF44336)
    val IconSuccessRate = Color(0xFF4CAF50)
    
    // Slider
    val SliderTrack = Color(0xFF4CAF50)
    val SliderThumb = Color(0xFF4CAF50)
    val SliderInactive = Color(0xFFBDBDBD)
    
    // Badge
    val BadgeUnsafe = Color(0xFFE91E63)
    val BadgeText = Color(0xFFFFFFFF)
    
    // Mode indicator
    val ModeSafe = Color(0xFF4CAF50)
    val ModeUnsafe = Color(0xFFE91E63)
    val ModeDeadlock = Color(0xFFFF9800)

    // Deadlock-specific icons
    val IconDeadlock = Color(0xFFFF9800)
    val IconPairs = Color(0xFF4CAF50)
    val IconStuck = Color(0xFFD32F2F)
    val IconPopcorn = Color(0xFFFFEB3B)
}
