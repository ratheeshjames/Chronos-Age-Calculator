package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ChronosRepository
import com.example.data.ChronosTab
import com.example.data.ThemeMode
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.shell.ChronosAppShell
import com.example.ui.theme.ChronosTheme
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import com.example.viewmodel.ChronosViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: ChronosViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsState()

      ChronosTheme(
        themeMode = uiState.preferences.themeMode,
        dynamicColor = uiState.preferences.isDynamicColor,
        amoledNight = uiState.preferences.isAmoledNight,
        accentColorHex = uiState.preferences.accentColorHex
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
          if (!uiState.preferences.hasCompletedOnboarding) {
            OnboardingScreen(
              onComplete = {
                viewModel.handleIntent(ChronosUiIntent.CompleteOnboarding)
              }
            )
          } else {
            ChronosAppShell(
              uiState = uiState,
              onIntent = { intent -> viewModel.handleIntent(intent) }
            )
          }
        }
      }
    }
  }
}

private fun buildPreviewState(tab: ChronosTab): ChronosUiState {
  val repo = ChronosRepository()
  val profiles = repo.profiles.value
  val active = profiles.first()
  val second = profiles.getOrElse(1) { active }
  val chrono = repo.calculateChronometry(active.birthDate, active.birthTime)
  return ChronosUiState(
    profiles = profiles,
    activeProfile = active,
    chronometry = chrono,
    planetaryAges = repo.calculatePlanetaryAges(chrono.totalDays),
    bioMetrics = repo.calculateBioMetrics(
      totalSecondsLived = chrono.totalSeconds,
      totalMinutesLived = chrono.totalMinutes,
      totalHoursLived = chrono.totalHours,
      ageYears = chrono.years,
      birthTime = active.birthTime,
      restingBpm = 72,
      dailySleepHours = 8f,
      targetLifespan = 80
    ),
    comparativeResult = repo.calculateComparison(active, second),
    secondaryProfileId = second.id,
    quotes = repo.getPhilosophicalQuotes(),
    selectedTab = tab
  )
}

@Preview(name = "Overview - Light", showBackground = true)
@Composable
fun OverviewScreenLightPreview() {
  ChronosTheme(themeMode = ThemeMode.LIGHT) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.OVERVIEW), onIntent = {})
    }
  }
}

@Preview(name = "Overview - Dark", showBackground = true)
@Composable
fun OverviewScreenDarkPreview() {
  ChronosTheme(themeMode = ThemeMode.DARK) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.OVERVIEW), onIntent = {})
    }
  }
}

@Preview(name = "Profiles - Light", showBackground = true)
@Composable
fun ProfilesScreenLightPreview() {
  ChronosTheme(themeMode = ThemeMode.LIGHT) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.PROFILES), onIntent = {})
    }
  }
}

@Preview(name = "Profiles - Dark", showBackground = true)
@Composable
fun ProfilesScreenDarkPreview() {
  ChronosTheme(themeMode = ThemeMode.DARK) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.PROFILES), onIntent = {})
    }
  }
}

@Preview(name = "Planetary - Light", showBackground = true)
@Composable
fun PlanetaryScreenLightPreview() {
  ChronosTheme(themeMode = ThemeMode.LIGHT) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.PLANETARY), onIntent = {})
    }
  }
}

@Preview(name = "Planetary - Dark", showBackground = true)
@Composable
fun PlanetaryScreenDarkPreview() {
  ChronosTheme(themeMode = ThemeMode.DARK) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.PLANETARY), onIntent = {})
    }
  }
}

@Preview(name = "Bio & Diff - Light", showBackground = true)
@Composable
fun BioDiffScreenLightPreview() {
  ChronosTheme(themeMode = ThemeMode.LIGHT) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.BIO_DIFF), onIntent = {})
    }
  }
}

@Preview(name = "Bio & Diff - Dark", showBackground = true)
@Composable
fun BioDiffScreenDarkPreview() {
  ChronosTheme(themeMode = ThemeMode.DARK) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.BIO_DIFF), onIntent = {})
    }
  }
}

@Preview(name = "Analytics & Life Grid - Light", showBackground = true)
@Composable
fun AnalyticsScreenLightPreview() {
  ChronosTheme(themeMode = ThemeMode.LIGHT) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.ANALYTICS), onIntent = {})
    }
  }
}

@Preview(name = "Analytics & Life Grid - AMOLED Dark", showBackground = true)
@Composable
fun AnalyticsScreenDarkPreview() {
  ChronosTheme(themeMode = ThemeMode.AMOLED) {
    Surface {
      ChronosAppShell(uiState = buildPreviewState(ChronosTab.ANALYTICS), onIntent = {})
    }
  }
}
