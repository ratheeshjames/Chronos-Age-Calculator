package com.example.ui.profiles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.Profile
import com.example.data.Relationship
import com.example.ui.theme.BottomSheetExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

val ProfilePaletteColors = listOf(
  0xFF1E50DE, // Celestial Blue
  0xFF7044BA, // Deep Violet
  0xFFB5236A, // Cosmic Magenta
  0xFF2A9D8F, // Emerald
  0xFFE76F51, // Terracotta Rust
  0xFFFFB703, // Solar Gold
  0xFF0077B6, // Deep Azure
  0xFF8338EC, // Electric Violet
  0xFF38B000, // Bio Green
  0xFFD00000, // Crimson Star
  0xFF583101, // Earth Sienna
  0xFF4A4E69  // Lunar Slate
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormBottomSheet(
  editingProfile: Profile?,
  onDismiss: () -> Unit,
  onSave: (String, LocalDate, LocalTime?, Relationship, Long, Boolean) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  var name by rememberSaveable(editingProfile?.id) { mutableStateOf(editingProfile?.name ?: "") }
  var birthDateEpochDay by rememberSaveable(editingProfile?.id) {
    mutableLongStateOf((editingProfile?.birthDate ?: LocalDate.of(1995, 6, 15)).toEpochDay())
  }
  var hasBirthTime by rememberSaveable(editingProfile?.id) { mutableStateOf(editingProfile?.birthTime != null) }
  var birthTimeSecondOfDay by rememberSaveable(editingProfile?.id) {
    mutableLongStateOf((editingProfile?.birthTime ?: LocalTime.of(9, 30)).toSecondOfDay().toLong())
  }
  var relationshipName by rememberSaveable(editingProfile?.id) {
    mutableStateOf((editingProfile?.relationship ?: Relationship.SELF).name)
  }
  var colorHex by rememberSaveable(editingProfile?.id) {
    mutableLongStateOf(editingProfile?.avatarColorHex ?: 0xFF1E50DE)
  }
  var isPrimary by rememberSaveable(editingProfile?.id) { mutableStateOf(editingProfile?.isPrimary ?: false) }

  var showDatePicker by rememberSaveable { mutableStateOf(false) }
  var showTimePicker by rememberSaveable { mutableStateOf(false) }

  val birthDate = LocalDate.ofEpochDay(birthDateEpochDay)
  val birthTime = LocalTime.ofSecondOfDay(birthTimeSecondOfDay.coerceIn(0L, 86399L))
  val relationship = Relationship.entries.find { it.name == relationshipName } ?: Relationship.SELF

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = BottomSheetExpressiveShape
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .imePadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp)
        .padding(bottom = 28.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (editingProfile == null) "New Profile Registry" else "Edit Profile",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      // Name Input
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Full Name", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        placeholder = { Text("e.g. Maya Lin", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_profile_name"),
        singleLine = true,
        shape = MaterialTheme.shapes.small
      )

      // Date of Birth Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Date of Birth", style = MaterialTheme.typography.labelMedium)
          Text(
            text = "$birthDate (${birthDate.dayOfWeek})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
          onClick = { showDatePicker = true },
          shape = MaterialTheme.shapes.small,
          modifier = Modifier.defaultMinSize(minHeight = 48.dp),
          contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
          Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Select", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
      }

      // Exact Time of Birth Toggle & Picker
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Exact Time of Birth", style = MaterialTheme.typography.bodyLarge)
          Text(
            text = if (hasBirthTime) "$birthTime" else "Not set (defaults to midnight)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(
          checked = hasBirthTime,
          onCheckedChange = {
            hasBirthTime = it
            if (it && !showTimePicker) showTimePicker = true
          }
        )
      }

      AnimatedVisibility(visible = hasBirthTime) {
        OutlinedButton(
          onClick = { showTimePicker = true },
          modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
          shape = MaterialTheme.shapes.small,
          contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
          Icon(Icons.Default.AccessTime, contentDescription = "Time", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Adjust Birth Time: $birthTime",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Relationship Category
      Text("Relationship Category", style = MaterialTheme.typography.labelMedium)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Relationship.entries.forEach { rel ->
          FilterChip(
            selected = relationship == rel,
            onClick = { relationshipName = rel.name },
            label = {
              Text(
                text = rel.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            shape = ChipExpressiveShape
          )
        }
      }

      // Avatar Color Swatches
      Text("Profile Identity Color", style = MaterialTheme.typography.labelMedium)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ProfilePaletteColors.forEach { hex ->
          val isSelected = colorHex == hex
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color(hex))
              .clickable { colorHex = hex }
              .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // Primary Me designation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Designate as Primary Self", style = MaterialTheme.typography.bodyLarge)
          Text(
            text = "Loads automatically as default entity",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(checked = isPrimary, onCheckedChange = { isPrimary = it })
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Save Button (flexible defaultMinSize instead of fixed height to prevent descender clipping)
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(
              name.trim(),
              birthDate,
              if (hasBirthTime) birthTime else null,
              relationship,
              colorHex,
              isPrimary
            )
          }
        },
        enabled = name.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .defaultMinSize(minHeight = 52.dp)
          .testTag("button_save_profile"),
        contentPadding = ButtonDefaults.ContentPadding,
        shape = MaterialTheme.shapes.medium
      ) {
        Text(
          text = "Save & Compute Chronometry",
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }

  // DatePickerDialog
  if (showDatePicker) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = birthDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
              birthDateEpochDay = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate().toEpochDay()
            }
            showDatePicker = false
          }
        ) {
          Text("Confirm Date")
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

  // TimePickerDialog
  if (showTimePicker) {
    val timePickerState = rememberTimePickerState(
      initialHour = birthTime.hour,
      initialMinute = birthTime.minute,
      is24Hour = false
    )
    AlertDialog(
      onDismissRequest = { showTimePicker = false },
      title = { Text("Exact Birth Time") },
      text = {
        TimePicker(state = timePickerState)
      },
      confirmButton = {
        TextButton(
          onClick = {
            birthTimeSecondOfDay = LocalTime.of(timePickerState.hour, timePickerState.minute).toSecondOfDay().toLong()
            showTimePicker = false
          }
        ) {
          Text("Confirm Time")
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
