package com.example.ui.analytics

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementBadge
import com.example.ui.theme.BottomSheetExpressiveShape
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.ui.theme.EmeraldMilestone
import com.example.ui.theme.MarsRust
import com.example.ui.theme.NeptuneCyan
import com.example.ui.theme.SolarGold
import com.example.ui.theme.VioletOrbit
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeGridAndBatterySection(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  modifier: Modifier = Modifier
) {
  val bio = uiState.bioMetrics
  val chrono = uiState.chronometry
  val prefs = uiState.preferences
  val birthYear = uiState.activeProfile?.birthDate?.year ?: 1996

  var inspectedYearCell by rememberSaveable { mutableStateOf<Int?>(null) }

  val infiniteTransition = rememberInfiniteTransition(label = "grid_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Life Expectancy Battery Gauge & Target Adjuster (F-073 & F-074)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_life_battery"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
      )
    ) {
      Column(modifier = Modifier.padding(22.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
              imageVector = Icons.Default.BatteryChargingFull,
              contentDescription = "Life Battery",
              tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Column {
              Text(
                text = "TEMPORAL LIFE BATTERY GAUGE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
              )
              Text(
                text = "Target Horizon: ${prefs.targetLifespanYears} Earth Years",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f)
              )
            }
          }

          val remainingPct = ((1f - bio.lifePercentage) * 100).roundToInt().coerceIn(0, 100)
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "$remainingPct% Left",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Black,
              color = EmeraldMilestone
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Custom Battery Visual Container
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .border(2.dp, MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .padding(4.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(bio.lifePercentage.coerceIn(0.04f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(EmeraldMilestone, SolarGold, MarsRust)
                  )
                )
            )
          }
          // Battery Terminal Cap
          Box(
            modifier = Modifier
              .width(6.dp)
              .height(16.dp)
              .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
              .background(MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f))
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val remainingYears = (prefs.targetLifespanYears - chrono.years).coerceAtLeast(0)
        val remainingDaysEst = (remainingYears * 365.25).toLong()
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${(bio.lifePercentage * 100).roundToInt()}% Elapsed (${chrono.years}y)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
          )
          Text(
            text = "~%,d days remaining".format(remainingDaysEst),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onTertiaryContainer
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Regional Longevity Presets
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val presets = listOf(
            "Global (73y)" to 73,
            "OECD (81y)" to 81,
            "Blue Zone (92y)" to 92,
            "Centenarian (100y)" to 100
          )
          presets.forEach { (label, yrs) ->
            FilterChip(
              selected = prefs.targetLifespanYears == yrs,
              onClick = {
                onIntent(
                  ChronosUiIntent.UpdateBiometricSliders(
                    prefs.restingBpm,
                    prefs.dailySleepHours,
                    yrs
                  )
                )
              },
              label = { Text(label) },
              shape = ChipExpressiveShape
            )
          }
        }

        Slider(
          value = prefs.targetLifespanYears.toFloat(),
          onValueChange = { newTarget ->
            onIntent(
              ChronosUiIntent.UpdateBiometricSliders(
                prefs.restingBpm,
                prefs.dailySleepHours,
                newTarget.roundToInt()
              )
            )
          },
          valueRange = 50f..110f,
          modifier = Modifier.testTag("slider_target_lifespan")
        )
      }
    }

    // 2. 100-Year Interactive Life Grid Matrix (F-071 & F-072)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_100_year_life_grid"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
              imageVector = Icons.Default.GridView,
              contentDescription = "Life Grid",
              tint = MaterialTheme.colorScheme.primary
            )
            Column {
              Text(
                text = "100-Year Life Grid Matrix",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Tap any year tile (0–99) to inspect milestones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 10x10 Grid of interactive Year Tiles
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          for (row in 0 until 10) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              for (col in 0 until 10) {
                val yearIndex = row * 10 + col
                val isLived = yearIndex < chrono.years
                val isCurrent = yearIndex == chrono.years
                val isWithinTarget = yearIndex < prefs.targetLifespanYears

                val cellBg = when {
                  isCurrent -> SolarGold
                  isLived -> MaterialTheme.colorScheme.primary
                  isWithinTarget -> MaterialTheme.colorScheme.surfaceVariant
                  else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                }

                val textColor = when {
                  isCurrent -> Color.Black
                  isLived -> MaterialTheme.colorScheme.onPrimary
                  else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                      alpha = if (isCurrent) pulseAlpha else 1f
                    }
                    .clip(RoundedCornerShape(6.dp))
                    .background(cellBg)
                    .clickable { inspectedYearCell = yearIndex }
                    .testTag("life_grid_cell_$yearIndex"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$yearIndex",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent || isLived) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          GridLegendItem("Lived (${chrono.years}y)", MaterialTheme.colorScheme.primary)
          GridLegendItem("Active Year", SolarGold)
          GridLegendItem("Horizon (${prefs.targetLifespanYears}y)", MaterialTheme.colorScheme.surfaceVariant)
        }
      }
    }

    // 3. Current Year Circular Progress Arc & Four Seasons Counter (F-075 & F-076)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Circular Arc Card (F-076)
      Card(
        modifier = Modifier
          .weight(1f)
          .testTag("card_year_arc"),
        shape = CardExpressiveShape
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Current Age Year",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(10.dp))
          val primaryColor = MaterialTheme.colorScheme.primary
          Box(
            modifier = Modifier.size(92.dp),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(84.dp)) {
              drawArc(
                color = primaryColor.copy(alpha = 0.18f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
              )
              drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = chrono.yearProgressFraction * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${(chrono.yearProgressFraction * 100).roundToInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "PASSED",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Year ${chrono.years} → ${chrono.years + 1}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Four Seasons Experiential Counter (F-075)
      Card(
        modifier = Modifier
          .weight(1.2f)
          .testTag("card_seasons_counter"),
        shape = CardExpressiveShape
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "Seasons",
              tint = SolarGold,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Seasons Experienced",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          val sCount = chrono.years
          SeasonQuadrantRow("🌸 Springs", sCount, EmeraldMilestone)
          Spacer(modifier = Modifier.height(6.dp))
          SeasonQuadrantRow("☀️ Summers", sCount, SolarGold)
          Spacer(modifier = Modifier.height(6.dp))
          SeasonQuadrantRow("🍂 Autumns", sCount, MarsRust)
          Spacer(modifier = Modifier.height(6.dp))
          SeasonQuadrantRow("❄️ Winters", sCount, NeptuneCyan)
        }
      }
    }

    // 4. 10,000-Day & 1,000,000-Hour Achievement Badges (F-076 & F-083)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_achievement_badges"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Badges",
            tint = SolarGold
          )
          Column {
            Text(
              text = "Temporal Achievement Badges",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "10,000-Day, Gigasecond & 1,000,000-Hour Honors",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        chrono.achievementBadges.forEach { badge ->
          AchievementBadgeRow(badge = badge)
        }
      }
    }

    // 5. "Time Spent on Earth" Infographic Breakdown (F-083)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_time_infographic"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Time Spent on Earth Infographic",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Estimated cumulative allocation across ${chrono.years} years (${"%,d".format(chrono.totalHours)} hours):",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val sleepYrs = bio.sleepYearsEquivalent
        val workYrs = (chrono.years * 0.22f)
        val mealsYrs = (chrono.years * 0.06f)
        val leisureYrs = (chrono.years - sleepYrs - workYrs - mealsYrs).coerceAtLeast(0.5f)

        InfographicBarRow("Sleep & Restoration", sleepYrs, VioletOrbit, sleepYrs / chrono.years.coerceAtLeast(1))
        InfographicBarRow("Vocation & Learning", workYrs, MaterialTheme.colorScheme.primary, 0.22f)
        InfographicBarRow("Culinary & Dining", mealsYrs, SolarGold, 0.06f)
        InfographicBarRow("Discovery & Connection", leisureYrs, EmeraldMilestone, leisureYrs / chrono.years.coerceAtLeast(1))
      }
    }
  }

  // Interactive Grid Year Inspector ModalBottomSheet (F-072)
  inspectedYearCell?.let { ageCell ->
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val calendarYr = birthYear + ageCell
    val statusLabel = when {
      ageCell < chrono.years -> "Completed Past Year"
      ageCell == chrono.years -> "Current Active Year of Life"
      else -> "Future Horizon Year (in ${ageCell - chrono.years} yrs)"
    }
    val stageAtYear = when {
      ageCell < 2 -> "Infancy"
      ageCell < 12 -> "Childhood"
      ageCell < 18 -> "Adolescence"
      ageCell < 35 -> "Early Adulthood"
      ageCell < 60 -> "Middle Adulthood"
      else -> "Golden Wisdom"
    }

    ModalBottomSheet(
      onDismissRequest = { inspectedYearCell = null },
      sheetState = sheetState,
      shape = BottomSheetExpressiveShape
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Year Tile #$ageCell • Calendar Year $calendarYr",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = statusLabel,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }
          IconButton(onClick = { inspectedYearCell = null }) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Life Stage Bracket: $stageAtYear", fontWeight = FontWeight.Bold)
            Text("Cumulative Days by End of Year #$ageCell: ~${"%,d".format(((ageCell + 1) * 365.25).toLong())} days")
            Text(
              text = String.format(
                "Mercury Orbits: %.1f • Mars Orbits: %.1f",
                ((ageCell + 1) * 365.25) / 87.97,
                ((ageCell + 1) * 365.25) / 686.98
              ),
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GridLegendItem(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(color)
    )
    Text(label, style = MaterialTheme.typography.labelSmall)
  }
}

@Composable
private fun SeasonQuadrantRow(label: String, count: Int, accent: Color) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    Text(
      text = "$count",
      style = MaterialTheme.typography.bodyMedium,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Black,
      color = accent
    )
  }
}

@Composable
private fun AchievementBadgeRow(badge: AchievementBadge) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .background(MaterialTheme.colorScheme.surface)
      .padding(14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = badge.title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold
        )
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(
              if (badge.isEarned) EmeraldMilestone.copy(alpha = 0.2f)
              else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (badge.isEarned) "EARNED 🏆" else "${(badge.progressFraction * 100).roundToInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = if (badge.isEarned) EmeraldMilestone else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      Text(
        text = "${badge.subtitle} • Target: ${badge.targetDateString}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { badge.progressFraction },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(CircleShape),
        color = if (badge.isEarned) EmeraldMilestone else MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
private fun InfographicBarRow(
  label: String,
  yearsSpent: Float,
  color: Color,
  fraction: Float
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
      Text(
        text = String.format("%.1f yrs", yearsSpent),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
    LinearProgressIndicator(
      progress = { fraction.coerceIn(0.05f, 1f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(CircleShape),
      color = color
    )
  }
}
