package com.example.ui.analytics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarSystem
import com.example.data.HapticIntensity
import com.example.data.ThemeMode
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.ui.theme.SolarGold
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import kotlin.math.roundToInt

private data class CardGradientPreset(
  val name: String,
  val colors: List<Color>
)

private val MilestoneCardPresets = listOf(
  CardGradientPreset("Cosmic Nebula", listOf(Color(0xFF0F172A), Color(0xFF3B0764), Color(0xFF1E3A8A))),
  CardGradientPreset("Solar Sunrise", listOf(Color(0xFF7C2D12), Color(0xFFB45309), Color(0xFFBE123C))),
  CardGradientPreset("Emerald Forest", listOf(Color(0xFF064E3B), Color(0xFF0F766E), Color(0xFF115E59))),
  CardGradientPreset("Cyber Neon", listOf(Color(0xFF1E1B4B), Color(0xFF701A75), Color(0xFF0284C7))),
  CardGradientPreset("Obsidian Gold", listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF4B5563)))
)

private val BrandAccentSwatches = listOf(
  "Sapphire" to 0xFF1E50DE,
  "Amethyst" to 0xFF7044BA,
  "Emerald" to 0xFF2A9D8F,
  "Crimson" to 0xFFB5236A,
  "Solar Rust" to 0xFFE76F51
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAndSettingsSection(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptics = LocalHapticFeedback.current
  val prefs = uiState.preferences
  val chrono = uiState.chronometry
  val active = uiState.activeProfile

  var showResetConfirmDialog by rememberSaveable { mutableStateOf(false) }

  val currentQuote = uiState.quotes.getOrNull(prefs.selectedQuoteIndex)
  val selectedPreset = MilestoneCardPresets.getOrElse(prefs.selectedCardThemeIndex) { MilestoneCardPresets.first() }

  fun triggerHapticIfEnabled() {
    if (prefs.hapticIntensity != HapticIntensity.OFF) {
      haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  fun buildFormattedCardText(): String {
    val profileName = active?.name ?: "Chronos Explorer"
    val quoteLine = currentQuote?.let { "\"${it.quote}\" — ${it.author}" } ?: ""
    return """
      ✨ CHRONOS TEMPORAL MILESTONE CARD ✨
      Profile: $profileName
      Exact Age: ${chrono.years} Years, ${chrono.months} Months, ${chrono.days} Days
      Total Lifespan: ${"%,d".format(chrono.totalDays)} Days • ${"%,d".format(chrono.totalHours)} Hours
      Heartbeats Lived: ~${"%,d".format(uiState.bioMetrics.estimatedHeartbeats)}
      Zodiac: ${chrono.westernZodiac} • Lunar: ${chrono.lunarZodiac}
      $quoteLine
    """.trimIndent()
  }

  fun copyTextToClipboard(label: String, text: String) {
    triggerHapticIfEnabled()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    onIntent(ChronosUiIntent.ShowToast("$label copied to clipboard!"))
  }

  fun shareTextIntent(title: String, text: String) {
    triggerHapticIfEnabled()
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, title)
      putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(sendIntent, title))
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Shareable Milestone Greeting Card Generator & Theme Customizer (F-077, F-080, F-081, F-082)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_milestone_greeting_generator"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Shareable Milestone Greeting Card",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Customize gradient theme & philosophical quote stamp",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          IconButton(
            onClick = {
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.CyclePhilosophicalQuote)
            },
            modifier = Modifier.testTag("btn_cycle_quote")
          ) {
            Icon(Icons.Default.Autorenew, contentDescription = "Next Quote", tint = MaterialTheme.colorScheme.primary)
          }
        }

        // Graphic Preview Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(CardExpressiveShape)
            .background(Brush.linearGradient(selectedPreset.colors))
            .padding(22.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "CHRONOS • ${selectedPreset.name.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = SolarGold,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.4.sp
              )
              Text(
                text = chrono.westernZodiac,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
              )
            }

            Text(
              text = active?.name ?: "Alex Vance",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "${chrono.years} Years • ${chrono.months} Months • ${chrono.days} Days",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = SolarGold
            )

            Text(
              text = "${"%,d".format(chrono.totalDays)} Days on Earth • ${"%,d".format(uiState.bioMetrics.estimatedHeartbeats)} Heartbeats",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.85f)
            )

            if (currentQuote != null) {
              Spacer(modifier = Modifier.height(6.dp))
              HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "\"${currentQuote.quote}\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.92f)
              )
              Text(
                text = "— ${currentQuote.author} (${currentQuote.era})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SolarGold
              )
            }
          }
        }

        // Card Gradient Theme Presets (F-081)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MilestoneCardPresets.forEachIndexed { idx, preset ->
            FilterChip(
              selected = prefs.selectedCardThemeIndex == idx,
              onClick = {
                triggerHapticIfEnabled()
                onIntent(ChronosUiIntent.SelectMilestoneCardTheme(idx))
              },
              label = { Text(preset.name) },
              shape = ChipExpressiveShape
            )
          }
        }

        // Copy & Share Actions (F-077 & F-080)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { copyTextToClipboard("Milestone Card", buildFormattedCardText()) },
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_copy_card_summary"),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Card", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
          Button(
            onClick = { shareTextIntent("Chronos Milestone Card", buildFormattedCardText()) },
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_share_card"),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Card", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }
      }
    }

    // 2. Structured JSON & CSV Life Log Exporter (F-078 & F-079)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_data_exporter"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Download, contentDescription = "Export", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "Structured Profile & Milestone Export",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Generate portable JSON or CSV tables for all ${uiState.profiles.size} profiles",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = {
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.ExportRegistryData("JSON"))
            },
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_export_json")
          ) {
            Text("Export JSON", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
          OutlinedButton(
            onClick = {
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.ExportRegistryData("CSV"))
            },
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_export_csv")
          ) {
            Text("Export CSV", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }
      }
    }

    // 3. Calendar System & Astronomical Julian Day Number (JDN) Engine (F-089, F-090, F-093, F-094)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_calendar_systems"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "Calendar Engine & Astronomical JDN",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Switch between Gregorian, ISO-8601 Week, and Julian O.S.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CalendarSystem.entries.forEach { sys ->
            FilterChip(
              selected = prefs.calendarSystem == sys,
              onClick = {
                triggerHapticIfEnabled()
                onIntent(ChronosUiIntent.UpdateCalendarSystem(sys))
              },
              label = { Text(sys.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
              shape = ChipExpressiveShape
            )
          }
        }

        // JDN & ISO-8601 Telemetry Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Active Format: ${prefs.calendarSystem.label}",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "• Birth Julian Day Number (JDN): ${"%,d".format(chrono.julianDayNumberBirth)} JD",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "• Current Astronomical JDN: ${"%,d".format(chrono.julianDayNumberCurrent)} JD",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "• ISO-8601 Week Date: ${chrono.isoWeekDateString} • ${chrono.julianCalendarDateString}",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }

        // Time-Zone Offset & DST Compensation (F-093 & F-094)
        Text(
          text = "Birth Time-Zone Offset: UTC${if (prefs.birthTimeZoneOffsetHours >= 0) "+${prefs.birthTimeZoneOffsetHours}" else "${prefs.birthTimeZoneOffsetHours}"}h",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold
        )
        Slider(
          value = prefs.birthTimeZoneOffsetHours.toFloat(),
          onValueChange = { newOffset ->
            onIntent(
              ChronosUiIntent.UpdateTimeZoneAndDst(
                newOffset.roundToInt(),
                prefs.compensateDst
              )
            )
          },
          valueRange = -12f..14f,
          steps = 25,
          modifier = Modifier.testTag("slider_timezone_offset")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Daylight Saving (DST) Auto-Fix", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Compensate for historical 1-hour seasonal shifts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Switch(
            checked = prefs.compensateDst,
            onCheckedChange = { enabled ->
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.UpdateTimeZoneAndDst(prefs.birthTimeZoneOffsetHours, enabled))
            }
          )
        }
      }
    }

    // 4. Personalization, Theme Engine & System Controls (F-084 – F-088, F-091, F-092, F-095, F-096, F-100)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_personalization_settings"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Palette, contentDescription = "Themes", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "M3 Expressive Theme & Personalization",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Dynamic Monet, AMOLED Pitch Dark, Warm Sunlight & Haptics",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Theme Mode Chips (F-084 – F-086)
        Text("Display Theme Mode", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ThemeMode.entries.forEach { mode ->
            FilterChip(
              selected = prefs.themeMode == mode,
              onClick = {
                triggerHapticIfEnabled()
                onIntent(ChronosUiIntent.UpdateThemeMode(mode))
              },
              label = { Text(mode.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
              shape = ChipExpressiveShape,
              modifier = Modifier.testTag("theme_chip_${mode.name.lowercase()}")
            )
          }
        }

        // Dynamic Wallpaper Color Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Android 12+ Dynamic Monet Palette", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Synchronize colors with device wallpaper", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Switch(
            checked = prefs.isDynamicColor,
            onCheckedChange = { dyn ->
              triggerHapticIfEnabled()
              onIntent(
                ChronosUiIntent.UpdateThemePreferences(
                  dynamicColor = dyn,
                  amoledNight = prefs.isAmoledNight,
                  tickerMs = prefs.tickerFrequencyMs,
                  useMillis = prefs.useMilliseconds
                )
              )
            }
          )
        }

        // Custom Brand Accent Palette Swatches (F-087)
        Text("Custom Brand Accent Swatch", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          BrandAccentSwatches.forEach { (name, hex) ->
            val isSelected = !prefs.isDynamicColor && prefs.accentColorHex == hex
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.clickable {
                triggerHapticIfEnabled()
                onIntent(ChronosUiIntent.UpdateAccentColor(hex))
              }
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color(hex))
                  .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                    shape = CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = name, tint = Color.White, modifier = Modifier.size(18.dp))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(name, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }

        HorizontalDivider()

        // Ticker Frequency & Ambient Low-Power Mode (F-088 & F-095)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(Icons.Default.Speed, contentDescription = "Ticker", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          Text("Clock Ticker Granularity & Power Mode", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }

        val freqOptions = listOf(
          "100ms High-Freq" to 100L,
          "1s Standard" to 1000L,
          "60s Ambient Eco" to 60_000L
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
          freqOptions.forEachIndexed { idx, (label, ms) ->
            SegmentedButton(
              selected = prefs.tickerFrequencyMs == ms,
              onClick = {
                triggerHapticIfEnabled()
                onIntent(
                  ChronosUiIntent.UpdateThemePreferences(
                    dynamicColor = prefs.isDynamicColor,
                    amoledNight = prefs.isAmoledNight,
                    tickerMs = ms,
                    useMillis = ms == 100L
                  )
                )
              },
              shape = SegmentedButtonDefaults.itemShape(index = idx, count = freqOptions.size)
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Haptic Feedback Intensity Profile (F-091)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(Icons.Default.Vibration, contentDescription = "Haptics", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          Text("Haptic Feedback Intensity", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
          HapticIntensity.entries.forEachIndexed { idx, intensity ->
            SegmentedButton(
              selected = prefs.hapticIntensity == intensity,
              onClick = {
                onIntent(ChronosUiIntent.UpdateHapticIntensity(intensity))
                if (intensity != HapticIntensity.OFF) {
                  haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
              },
              shape = SegmentedButtonDefaults.itemShape(index = idx, count = HapticIntensity.entries.size)
            ) {
              Text(
                text = intensity.label,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Audio Chimes on Milestone (F-092)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Celebratory Milestone Audio Chimes", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("Sound feedback when countdowns reach zero", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Switch(
            checked = prefs.audioChimesEnabled,
            onCheckedChange = { enabled ->
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.ToggleAudioChimes(enabled))
            }
          )
        }

        HorizontalDivider()

        // Onboarding Re-Tour (F-100) & Reset Defaults (F-096)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              triggerHapticIfEnabled()
              onIntent(ChronosUiIntent.RelaunchOnboardingTour)
            },
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_relaunch_onboarding"),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Tour", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Onboarding Tour", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }

          OutlinedButton(
            onClick = { showResetConfirmDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
              .weight(1f)
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_reset_defaults"),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Defaults", maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }
      }
    }
  }

  // Export JSON / CSV Preview Dialog (F-078 & F-079)
  uiState.exportedDataPreview?.let { (format, content) ->
    AlertDialog(
      onDismissRequest = { onIntent(ChronosUiIntent.DismissExportPreview) },
      title = { Text("Exported $format Data") },
      text = {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            copyTextToClipboard("Chronos $format", content)
            onIntent(ChronosUiIntent.DismissExportPreview)
          }
        ) {
          Text("Copy $format")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            shareTextIntent("Chronos Registry ($format)", content)
            onIntent(ChronosUiIntent.DismissExportPreview)
          }
        ) {
          Text("Share")
        }
      }
    )
  }

  // Factory Reset Confirmation Dialog (F-096)
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Reset Preferences?") },
      text = {
        Text("This will restore theme mode, accent swatches, biometric sliders, calendar format, and ticker granularity to their clean factory defaults.")
      },
      confirmButton = {
        Button(
          onClick = {
            onIntent(ChronosUiIntent.ResetSettingsToDefaults)
            showResetConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Reset All")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
