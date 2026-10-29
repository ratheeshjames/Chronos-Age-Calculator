package com.example.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ExpressiveShapes = Shapes(
  extraSmall = RoundedCornerShape(CornerSize(12.dp)),
  small = RoundedCornerShape(CornerSize(16.dp)),     // Chips & small interactive tokens
  medium = RoundedCornerShape(CornerSize(22.dp)),    // Small cards & snackbars
  large = RoundedCornerShape(CornerSize(28.dp)),     // Primary content cards
  extraLarge = RoundedCornerShape(CornerSize(32.dp)) // Modal dialogs & bottom sheets
)

// Granular tokens for specialized M3E containers
val PillShape = RoundedCornerShape(percent = 50)
val CardExpressiveShape = RoundedCornerShape(28.dp)
val BottomSheetExpressiveShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val ChipExpressiveShape = RoundedCornerShape(16.dp)
val MetricBoxShape = RoundedCornerShape(20.dp)
