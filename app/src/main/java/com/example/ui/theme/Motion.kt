package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp

object ChronosMotion {
  // Custom M3 Expressive bouncy spring spec for Floats
  val ExpressiveSpringFloat: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
  )

  // Custom M3 Expressive bouncy spring spec for Dp dimensions
  val ExpressiveSpringDp: SpringSpec<Dp> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow
  )

  val EmphasizedDecel = tween<Float>(
    durationMillis = 450,
    easing = androidx.compose.animation.core.FastOutSlowInEasing
  )

  val QuickResponsiveSpring: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMedium
  )

  const val CardExpandDuration = 350
  const val NumericOdometerDuration = 250
}
