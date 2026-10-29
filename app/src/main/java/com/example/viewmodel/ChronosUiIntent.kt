package com.example.viewmodel

import com.example.data.CalendarSystem
import com.example.data.ChronosTab
import com.example.data.HapticIntensity
import com.example.data.Profile
import com.example.data.Relationship
import com.example.data.ThemeMode
import java.time.LocalDate
import java.time.LocalTime

sealed interface ChronosUiIntent {
  data class SelectTab(val tab: ChronosTab) : ChronosUiIntent
  data class SelectActiveProfile(val id: String) : ChronosUiIntent
  data class SelectSecondaryProfile(val id: String) : ChronosUiIntent
  object SwapComparisonProfiles : ChronosUiIntent
  data class SetTargetDate(val target: LocalDate?) : ChronosUiIntent
  object OpenCreateProfileSheet : ChronosUiIntent
  data class OpenEditProfileSheet(val profile: Profile) : ChronosUiIntent
  object CloseProfileSheet : ChronosUiIntent
  data class SaveProfile(
    val id: String?,
    val name: String,
    val birthDate: LocalDate,
    val birthTime: LocalTime?,
    val relationship: Relationship,
    val colorHex: Long,
    val isPrimary: Boolean
  ) : ChronosUiIntent
  data class DeleteProfile(val id: String) : ChronosUiIntent
  data class SetPrimaryProfile(val id: String) : ChronosUiIntent
  data class AddCustomMilestone(
    val title: String,
    val targetDate: LocalDate,
    val category: String,
    val colorHex: Long
  ) : ChronosUiIntent
  data class DeleteCustomMilestone(val id: String) : ChronosUiIntent
  data class ToggleCohortSelection(val profileId: String) : ChronosUiIntent
  data class UpdateDateArithmetic(
    val baseDate: LocalDate,
    val isAddition: Boolean,
    val years: Int,
    val months: Int,
    val weeks: Int,
    val days: Int
  ) : ChronosUiIntent
  data class UpdateBiometricSliders(
    val restingBpm: Int,
    val sleepHours: Float,
    val targetLifespan: Int
  ) : ChronosUiIntent
  data class UpdateThemePreferences(
    val dynamicColor: Boolean,
    val amoledNight: Boolean,
    val tickerMs: Long,
    val useMillis: Boolean
  ) : ChronosUiIntent
  data class UpdateThemeMode(val mode: ThemeMode) : ChronosUiIntent
  data class UpdateAccentColor(val colorHex: Long) : ChronosUiIntent
  data class UpdateCalendarSystem(val system: CalendarSystem) : ChronosUiIntent
  data class UpdateHapticIntensity(val intensity: HapticIntensity) : ChronosUiIntent
  data class ToggleAudioChimes(val enabled: Boolean) : ChronosUiIntent
  data class UpdateTimeZoneAndDst(val offsetHours: Int, val compensateDst: Boolean) : ChronosUiIntent
  object CyclePhilosophicalQuote : ChronosUiIntent
  data class SelectMilestoneCardTheme(val index: Int) : ChronosUiIntent
  data class ExportRegistryData(val format: String) : ChronosUiIntent
  object DismissExportPreview : ChronosUiIntent
  object RelaunchOnboardingTour : ChronosUiIntent
  object ResetSettingsToDefaults : ChronosUiIntent
  object TriggerCelebration : ChronosUiIntent
  object DismissCelebration : ChronosUiIntent
  object CompleteOnboarding : ChronosUiIntent
  object DismissToast : ChronosUiIntent
  data class ShowToast(val message: String) : ChronosUiIntent
}
