package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.ThemeMode

val ExpressiveLightColorScheme = lightColorScheme(
  primary = PrimaryLight,
  onPrimary = OnPrimaryLight,
  primaryContainer = PrimaryContainerLight,
  onPrimaryContainer = OnPrimaryContainerLight,
  secondary = SecondaryLight,
  onSecondary = OnSecondaryLight,
  secondaryContainer = SecondaryContainerLight,
  onSecondaryContainer = OnSecondaryContainerLight,
  tertiary = TertiaryLight,
  onTertiary = OnTertiaryLight,
  tertiaryContainer = TertiaryContainerLight,
  onTertiaryContainer = OnTertiaryContainerLight,
  background = BackgroundLight,
  onBackground = OnBackgroundLight,
  surface = SurfaceLight,
  onSurface = OnSurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight,
  outlineVariant = OutlineVariantLight
)

val ExpressiveWarmSunlightColorScheme = lightColorScheme(
  primary = Color(0xFF9C4100),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFFFDBCA),
  onPrimaryContainer = Color(0xFF331100),
  secondary = Color(0xFF755846),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFFFDBCA),
  onSecondaryContainer = Color(0xFF2B1609),
  tertiary = Color(0xFF626033),
  onTertiary = Color(0xFFFFFFFF),
  tertiaryContainer = Color(0xFFE8E4AC),
  onTertiaryContainer = Color(0xFF1D1D00),
  background = Color(0xFFFFF8F5),
  onBackground = Color(0xFF221A15),
  surface = Color(0xFFFFFBF9),
  onSurface = Color(0xFF221A15),
  surfaceVariant = Color(0xFFF4DED3),
  onSurfaceVariant = Color(0xFF52443C)
)

val ExpressiveDarkColorScheme = darkColorScheme(
  primary = PrimaryDark,
  onPrimary = OnPrimaryDark,
  primaryContainer = PrimaryContainerDark,
  onPrimaryContainer = OnPrimaryContainerDark,
  secondary = SecondaryDark,
  onSecondary = OnSecondaryDark,
  secondaryContainer = SecondaryContainerDark,
  onSecondaryContainer = OnSecondaryContainerDark,
  tertiary = TertiaryDark,
  onTertiary = OnTertiaryDark,
  tertiaryContainer = TertiaryContainerDark,
  onTertiaryContainer = OnTertiaryContainerDark,
  background = BackgroundDark,
  onBackground = OnBackgroundDark,
  surface = SurfaceDark,
  onSurface = OnSurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark,
  outlineVariant = OutlineVariantDark
)

val ExpressiveAmoledColorScheme = ExpressiveDarkColorScheme.copy(
  background = AmoledBackground,
  surface = AmoledSurface,
  surfaceVariant = Color(0xFF141720)
)

@Composable
fun ChronosTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  themeMode: ThemeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
  dynamicColor: Boolean = false,
  amoledNight: Boolean = false,
  accentColorHex: Long = 0xFF1E50DE,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val effectiveDark = when (themeMode) {
    ThemeMode.SYSTEM -> darkTheme
    ThemeMode.LIGHT, ThemeMode.WARM_SUNLIGHT -> false
    ThemeMode.DARK, ThemeMode.AMOLED -> true
  }
  val effectiveAmoled = amoledNight || themeMode == ThemeMode.AMOLED

  val baseScheme: ColorScheme = when {
    themeMode == ThemeMode.WARM_SUNLIGHT -> ExpressiveWarmSunlightColorScheme
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      if (effectiveDark) {
        if (effectiveAmoled) {
          dynamicDarkColorScheme(context).copy(
            background = AmoledBackground,
            surface = AmoledSurface
          )
        } else {
          dynamicDarkColorScheme(context)
        }
      } else {
        dynamicLightColorScheme(context)
      }
    }
    effectiveDark -> if (effectiveAmoled) ExpressiveAmoledColorScheme else ExpressiveDarkColorScheme
    else -> ExpressiveLightColorScheme
  }

  val finalScheme = if (!dynamicColor && accentColorHex != 0xFF1E50DE && themeMode != ThemeMode.WARM_SUNLIGHT) {
    val accent = Color(accentColorHex)
    if (effectiveDark) {
      baseScheme.copy(
        primary = accent,
        primaryContainer = accent.copy(alpha = 0.28f),
        onPrimaryContainer = Color.White
      )
    } else {
      baseScheme.copy(
        primary = accent,
        primaryContainer = accent.copy(alpha = 0.16f),
        onPrimaryContainer = Color(0xFF111318)
      )
    }
  } else {
    baseScheme
  }

  MaterialTheme(
    colorScheme = finalScheme,
    typography = ChronosTypography,
    shapes = ExpressiveShapes,
    content = content
  )
}
