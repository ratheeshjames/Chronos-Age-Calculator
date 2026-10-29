package com.example.ui.biodiff

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BioMetricResult
import com.example.data.ChronosPreferences
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.EmeraldMilestone
import com.example.ui.theme.MarsRust
import com.example.ui.theme.NeptuneCyan
import com.example.ui.theme.VioletOrbit
import kotlin.math.roundToInt

@Composable
fun BiologicalMetricsSection(
  bio: BioMetricResult,
  preferences: ChronosPreferences,
  onUpdateBiometrics: (restingBpm: Int, sleepHours: Float, targetLifespan: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "bio_pulse")
  val beatPeriodMs = (60_000f / preferences.restingBpm.coerceAtLeast(40)).toInt()
  val heartScale by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.18f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = beatPeriodMs / 2),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heart_scale"
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Live Heartbeat & Pulse Card (F-051 & F-052)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_bio_heartbeats"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer
      )
    ) {
      Column(modifier = Modifier.padding(22.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFE63946).copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Beating Heart",
                tint = Color(0xFFE63946),
                modifier = Modifier
                  .size(24.dp)
                  .scale(heartScale)
              )
            }
            Column {
              Text(
                text = "ESTIMATED LIFETIME HEARTBEATS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
              )
              Text(
                text = "Live Cardiac Chronometry",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${preferences.restingBpm} BPM",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE63946)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "%,d".format(bio.estimatedHeartbeats),
          style = MaterialTheme.typography.headlineLarge,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Calibrate Resting Heart Rate (50 – 100 BPM)",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Slider(
          value = preferences.restingBpm.toFloat(),
          onValueChange = { newBpm ->
            onUpdateBiometrics(
              newBpm.roundToInt(),
              preferences.dailySleepHours,
              preferences.targetLifespanYears
            )
          },
          valueRange = 50f..100f,
          modifier = Modifier.testTag("slider_resting_bpm")
        )
      }
    }

    // 2. Respiratory Cycles & Ocular Blinks Grid (F-053 & F-056)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        modifier = Modifier
          .weight(1f)
          .testTag("card_bio_breaths"),
        shape = MaterialTheme.shapes.large
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.Air,
              contentDescription = "Breaths",
              tint = NeptuneCyan,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Breaths Taken",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "%,d".format(bio.estimatedBreaths),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "~15 respiratory cycles/min",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Card(
        modifier = Modifier
          .weight(1f)
          .testTag("card_bio_blinks"),
        shape = MaterialTheme.shapes.large
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = "Blinks",
              tint = EmeraldMilestone,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Lifetime Blinks",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "%,d".format(bio.estimatedBlinks),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = EmeraldMilestone
          )
          Text(
            text = "~17 blinks/waking min",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 3. Sleep Architecture & REM / Deep Phase Breakdown (F-054 & F-055)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_bio_sleep"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
              imageVector = Icons.Default.Bedtime,
              contentDescription = "Sleep",
              tint = VioletOrbit
            )
            Column {
              Text(
                text = "Cumulative Sleep Architecture",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = String.format("%,d total hours • %.1f years asleep", bio.sleepHoursAccumulated, bio.sleepYearsEquivalent),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Text(
            text = String.format("%.1fh / day", preferences.dailySleepHours),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = VioletOrbit
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Sleep Bar (REM 22%, Deep 20%, Light 58%)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape)
        ) {
          Box(
            modifier = Modifier
              .weight(0.22f)
              .fillMaxHeight()
              .background(VioletOrbit)
          )
          Box(
            modifier = Modifier
              .weight(0.20f)
              .fillMaxHeight()
              .background(MaterialTheme.colorScheme.primary)
          )
          Box(
            modifier = Modifier
              .weight(0.58f)
              .fillMaxHeight()
              .background(NeptuneCyan)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          SleepStageLegendItem(
            label = "REM Dream (22%)",
            hours = bio.remSleepHours,
            color = VioletOrbit
          )
          SleepStageLegendItem(
            label = "Deep Delta (20%)",
            hours = bio.deepSleepHours,
            color = MaterialTheme.colorScheme.primary
          )
          SleepStageLegendItem(
            label = "Light NREM (58%)",
            hours = bio.lightSleepHours,
            color = NeptuneCyan
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Adjust Average Daily Sleep (4h – 12h)",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Slider(
          value = preferences.dailySleepHours,
          onValueChange = { newSleep ->
            val stepped = (newSleep * 2f).roundToInt() / 2f
            onUpdateBiometrics(
              preferences.restingBpm,
              stepped,
              preferences.targetLifespanYears
            )
          },
          valueRange = 4f..12f,
          steps = 15,
          modifier = Modifier.testTag("slider_daily_sleep")
        )
      }
    }

    // 4. Circadian Chronotype & Developmental Life Stage (F-057 & F-058)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        modifier = Modifier
          .weight(1f)
          .testTag("card_circadian_chronotype"),
        shape = MaterialTheme.shapes.large
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = "Circadian",
              tint = MarsRust,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Chronotype",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = bio.circadianChronotype,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Peak: ${bio.circadianPeakWindow}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Card(
        modifier = Modifier
          .weight(1f)
          .testTag("card_life_stage"),
        shape = MaterialTheme.shapes.large
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = Icons.Default.Timeline,
              contentDescription = "Life Stage",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Life Bracket",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = bio.lifeStageName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = bio.lifeStageDescription,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
          )
        }
      }
    }
  }
}

@Composable
private fun SleepStageLegendItem(
  label: String,
  hours: Long,
  color: Color
) {
  Column {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
    Text(
      text = "%,d hrs".format(hours),
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Bold
    )
  }
}
