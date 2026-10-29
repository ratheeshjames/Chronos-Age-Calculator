package com.example.ui.overview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AstrologyTriviaCard
import com.example.ui.components.BirthdayForecastCard
import com.example.ui.components.InteractiveChronosUnitCell
import com.example.ui.components.MilestoneTrackerCards
import com.example.ui.components.RollingNumberText
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.ui.theme.NumericOdometerStyle
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val active = uiState.activeProfile
  val chrono = uiState.chronometry

  var showLeapDialog by rememberSaveable { mutableStateOf(false) }
  var showDatePicker by rememberSaveable { mutableStateOf(false) }
  var showTimePicker by rememberSaveable { mutableStateOf(false) }
  var showMillis by rememberSaveable { mutableStateOf(false) }

  fun copyToClipboard(label: String, value: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
    onIntent(ChronosUiIntent.ShowToast("Copied $label: $value"))
  }

  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Profile Carousel Strip
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        uiState.profiles.forEach { profile ->
          val isSelected = profile.id == active?.id
          FilterChip(
            selected = isSelected,
            onClick = { onIntent(ChronosUiIntent.SelectActiveProfile(profile.id)) },
            label = {
              Text(
                text = profile.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            leadingIcon = {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(Color(profile.avatarColorHex))
              )
            },
            shape = ChipExpressiveShape,
            modifier = Modifier.testTag("overview_chip_${profile.id}")
          )
        }
      }
    }

    // 2. Main Live Exact Age Card (Features 1 to 4)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("main_age_card"),
        shape = CardExpressiveShape,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        )
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Header Row with quick date & time edit triggers (>= 48.dp touch target)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CHRONOMETRIC AGE",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.8.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              IconButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.size(48.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CalendarToday,
                  contentDescription = "Edit DOB",
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(20.dp)
                )
              }
              IconButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.size(48.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AccessTime,
                  contentDescription = "Edit Birth Time",
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Primary Units Breakdown: Years, Months, Days
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            InteractiveChronosUnitCell(
              value = chrono.years,
              label = "YEARS",
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              onClick = { copyToClipboard("Years", "${chrono.years}") }
            )
            Text(text = "•", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            InteractiveChronosUnitCell(
              value = chrono.months,
              label = "MONTHS",
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              onClick = { copyToClipboard("Months", "${chrono.months}") }
            )
            Text(text = "•", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            InteractiveChronosUnitCell(
              value = chrono.days,
              label = "DAYS",
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              onClick = { copyToClipboard("Days", "${chrono.days}") }
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Live Ticking Sub-Day Time Capsule (Hours, Minutes, Seconds, Millis)
          Row(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
              .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val timeText = if (showMillis) {
              String.format("%02d:%02d:%02d.%03d", chrono.hours, chrono.minutes, chrono.seconds, chrono.millis)
            } else {
              String.format("%02d:%02d:%02d", chrono.hours, chrono.minutes, chrono.seconds)
            }

            RollingNumberText(
              value = timeText,
              style = NumericOdometerStyle,
              color = MaterialTheme.colorScheme.primary
            )

            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "TICK",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 9.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Precision toggles & leap day chips
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AssistChip(
              onClick = { showMillis = !showMillis },
              label = { Text(if (showMillis) "Hide Millis" else "Show Millis") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = "Millis",
                  modifier = Modifier.size(16.dp)
                )
              }
            )

            AssistChip(
              onClick = { showLeapDialog = true },
              label = { Text("${chrono.leapDaysLived} Leap Days") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Leap info",
                  modifier = Modifier.size(16.dp)
                )
              }
            )
          }
        }
      }
    }

    // 3. Total Elapsed Units Grid (Features 5 to 9)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Total Elapsed Lifespan",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Tap any to copy",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Total Days
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { copyToClipboard("Total Days", "%,d".format(chrono.totalDays)) },
          shape = MaterialTheme.shapes.medium
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Days", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "%,d".format(chrono.totalDays),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
        // Total Weeks
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { copyToClipboard("Total Weeks", "%,d".format(chrono.totalWeeks)) },
          shape = MaterialTheme.shapes.medium
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Weeks", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "%,d".format(chrono.totalWeeks),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Total Hours
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { copyToClipboard("Total Hours", "%,d".format(chrono.totalHours)) },
          shape = MaterialTheme.shapes.medium
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Hours", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "%,d".format(chrono.totalHours),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.tertiary
            )
          }
        }
        // Total Minutes
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { copyToClipboard("Total Minutes", "%,d".format(chrono.totalMinutes)) },
          shape = MaterialTheme.shapes.medium
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Minutes", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "%,d".format(chrono.totalMinutes),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    item {
      // Total Seconds Live Odometer
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { copyToClipboard("Total Seconds", "%,d".format(chrono.totalSeconds)) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Total Seconds Lived", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "%,d".format(chrono.totalSeconds),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy Seconds",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // 4. Next Birthday Live Countdown (Feature 30)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardExpressiveShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Next Birthday Countdown",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${chrono.nextBirthdayDaysRemaining}d ${chrono.nextBirthdayHoursRemaining}h ${chrono.nextBirthdayMinutesRemaining}m ${chrono.nextBirthdaySecondsRemaining}s",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Turning ${chrono.years + 1} on ${chrono.nextBirthdayDate.dayOfWeek}, ${chrono.nextBirthdayDate}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Cake,
              contentDescription = "Birthday",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }

    // 5. Next 5 Birthdays Forecast Accordion (Feature 31)
    item {
      BirthdayForecastCard(upcomingList = chrono.upcomingBirthdays)
    }

    // 6. Milestone Tracker Cards (Decade, Half-Birthday, Golden BDay, 10k Days, 1B Secs)
    item {
      MilestoneTrackerCards(chrono = chrono)
    }

    // 7. Astrology & Cultural Trivia (Features 15, 16, 17)
    item {
      AstrologyTriviaCard(chrono = chrono)
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Leap-Year Dialog
  if (showLeapDialog) {
    AlertDialog(
      onDismissRequest = { showLeapDialog = false },
      title = { Text("Intercalary Leap Days") },
      text = {
        Text(
          "Because you were born in ${active?.birthDate?.year ?: 2000}, you have lived through exactly ${chrono.leapDaysLived} leap days (February 29ths). Each leap year adds an extra 24 hours (86,400 seconds) to calendar synchronicity with Earth's orbit around the sun."
        )
      },
      confirmButton = {
        TextButton(onClick = { showLeapDialog = false }) {
          Text("Got It")
        }
      }
    )
  }

  // Inline Quick Date Picker
  if (showDatePicker && active != null) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = active.birthDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(onClick = {
          datePickerState.selectedDateMillis?.let { millis ->
            val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
            onIntent(
              ChronosUiIntent.SaveProfile(
                id = active.id,
                name = active.name,
                birthDate = newDate,
                birthTime = active.birthTime,
                relationship = active.relationship,
                colorHex = active.avatarColorHex,
                isPrimary = active.isPrimary
              )
            )
          }
          showDatePicker = false
        }) {
          Text("Save Date")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text("Cancel")
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }

  // Inline Quick Time Picker
  if (showTimePicker && active != null) {
    val initialTime = active.birthTime ?: LocalTime.of(12, 0)
    val timePickerState = rememberTimePickerState(
      initialHour = initialTime.hour,
      initialMinute = initialTime.minute,
      is24Hour = false
    )

    AlertDialog(
      onDismissRequest = { showTimePicker = false },
      title = { Text("Set Birth Time") },
      text = {
        TimePicker(state = timePickerState)
      },
      confirmButton = {
        TextButton(onClick = {
          val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
          onIntent(
            ChronosUiIntent.SaveProfile(
              id = active.id,
              name = active.name,
              birthDate = active.birthDate,
              birthTime = newTime,
              relationship = active.relationship,
              colorHex = active.avatarColorHex,
              isPrimary = active.isPrimary
            )
          )
          showTimePicker = false
        }) {
          Text("Save Time")
        }
      },
      dismissButton = {
        TextButton(onClick = { showTimePicker = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
