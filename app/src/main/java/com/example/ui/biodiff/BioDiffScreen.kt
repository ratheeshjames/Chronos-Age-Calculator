package com.example.ui.biodiff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private enum class BioDiffSubTab(val label: String) {
  ALL("All Suite"),
  BIO("Bio Rhythm"),
  DIFF("Time Diff"),
  ARITHMETIC("Date Math")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioDiffScreen(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  var selectedSubTab by rememberSaveable { mutableIntStateOf(0) }
  val tabs = BioDiffSubTab.entries

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item(key = "biodiff_header_tabs") {
        Spacer(modifier = Modifier.height(4.dp))
        SingleChoiceSegmentedButtonRow(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("biodiff_segmented_row")
        ) {
          tabs.forEachIndexed { index, tab ->
            SegmentedButton(
              selected = selectedSubTab == index,
              onClick = { selectedSubTab = index },
              shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size)
            ) {
              Text(
                text = tab.label,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      val activeMode = tabs[selectedSubTab]

      // 1. Biological Metrics Section (F-051 to F-058)
      if (activeMode == BioDiffSubTab.ALL || activeMode == BioDiffSubTab.BIO) {
        item(key = "biodiff_section_bio") {
          BiologicalMetricsSection(
            bio = uiState.bioMetrics,
            preferences = uiState.preferences,
            onUpdateBiometrics = { bpm, sleep, lifespan ->
              onIntent(ChronosUiIntent.UpdateBiometricSliders(bpm, sleep, lifespan))
            }
          )
        }
      }

      // 2. Comparative Profile Diff & Cohort Analyzer (F-059 to F-065, F-070)
      if (activeMode == BioDiffSubTab.ALL || activeMode == BioDiffSubTab.DIFF) {
        item(key = "biodiff_section_diff") {
          ComparativeProfileSection(
            uiState = uiState,
            onIntent = onIntent
          )
        }
      }

      // 3. Date Arithmetic, Workdays & Custom Milestones (F-037, F-038, F-066 to F-069)
      if (activeMode == BioDiffSubTab.ALL || activeMode == BioDiffSubTab.ARITHMETIC) {
        item(key = "biodiff_section_arithmetic") {
          DateArithmeticAndMilestonesSection(
            uiState = uiState,
            onIntent = onIntent
          )
        }
      }

      item(key = "biodiff_bottom_spacer") {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // Celebratory Confetti Burst Overlay (F-038)
    AnimatedVisibility(
      visible = uiState.isCelebrationActive,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      ConfettiCelebrationOverlay(
        onFinished = {
          onIntent(ChronosUiIntent.DismissCelebration)
          onIntent(ChronosUiIntent.ShowToast("Milestone Celebrated! 🎉"))
        }
      )
    }
  }
}

@Composable
private fun ConfettiCelebrationOverlay(
  onFinished: () -> Unit
) {
  val progress = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 1600)
    )
    onFinished()
  }

  val palette = listOf(
    Color(0xFFFFB703),
    Color(0xFF1E50DE),
    Color(0xFFE76F51),
    Color(0xFF2A9D8F),
    Color(0xFFB5236A),
    Color(0xFF8338EC)
  )

  Canvas(modifier = Modifier.fillMaxSize()) {
    val center = Offset(size.width / 2f, size.height / 2.5f)
    val maxDist = size.minDimension * 0.7f
    val p = progress.value

    for (i in 0 until 48) {
      val angle = (i * (360.0 / 48.0)) * (PI / 180.0)
      val speed = 0.4f + ((i % 5) * 0.15f)
      val dist = maxDist * p * speed
      val gravityDrop = p * p * 260f
      val x = center.x + (cos(angle) * dist).toFloat()
      val y = center.y + (sin(angle) * dist).toFloat() + gravityDrop
      val color = palette[i % palette.size].copy(alpha = (1f - p).coerceIn(0f, 1f))

      drawCircle(
        color = color,
        radius = (8f - (i % 4)).dp.toPx(),
        center = Offset(x, y)
      )
    }
  }
}
