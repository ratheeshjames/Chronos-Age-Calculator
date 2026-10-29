package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChronosRepository
import com.example.data.CustomMilestone
import com.example.data.Profile
import com.example.data.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class ChronosViewModel(
  private val repository: ChronosRepository = ChronosRepository()
) : ViewModel() {

  private val _uiState = MutableStateFlow(
    ChronosUiState(quotes = repository.getPhilosophicalQuotes())
  )
  val uiState: StateFlow<ChronosUiState> = _uiState.asStateFlow()

  private var tickerJob: Job? = null

  init {
    viewModelScope.launch {
      repository.profiles.collect { profileList ->
        _uiState.update { current ->
          val activeId = current.activeProfile?.id ?: repository.activeProfileId.value
          val active = profileList.find { it.id == activeId } ?: profileList.firstOrNull()
          val secondary = profileList.find { it.id == current.secondaryProfileId }
            ?: profileList.firstOrNull { it.id != active?.id }
          val cohortIds = current.selectedCohortProfileIds.ifEmpty {
            profileList.take(3).map { it.id }.toSet()
          }
          current.copy(
            profiles = profileList,
            activeProfile = active,
            secondaryProfileId = secondary?.id,
            selectedCohortProfileIds = cohortIds
          )
        }
        recalculateAll()
      }
    }

    viewModelScope.launch {
      repository.activeProfileId.collect { activeId ->
        _uiState.update { current ->
          val active = current.profiles.find { it.id == activeId } ?: current.activeProfile
          val secondaryId = if (current.secondaryProfileId == activeId) {
            current.profiles.firstOrNull { it.id != activeId }?.id
          } else {
            current.secondaryProfileId
          }
          current.copy(
            activeProfile = active,
            secondaryProfileId = secondaryId
          )
        }
        recalculateAll()
      }
    }

    viewModelScope.launch {
      repository.customMilestones.collect { milestones ->
        _uiState.update { it.copy(customMilestones = milestones) }
      }
    }

    viewModelScope.launch {
      repository.preferences.collect { prefs ->
        val oldFreq = _uiState.value.preferences.tickerFrequencyMs
        _uiState.update { it.copy(preferences = prefs) }
        if (oldFreq != prefs.tickerFrequencyMs || tickerJob == null) {
          startTicker(prefs.tickerFrequencyMs)
        }
        recalculateAll()
      }
    }

    startTicker(1000L)
  }

  private fun startTicker(intervalMs: Long) {
    tickerJob?.cancel()
    tickerJob = viewModelScope.launch {
      while (isActive) {
        tickChronometry()
        delay(intervalMs.coerceAtLeast(100L))
      }
    }
  }

  private fun tickChronometry() {
    val active = _uiState.value.activeProfile ?: return
    val prefs = _uiState.value.preferences
    val rawTarget = _uiState.value.targetDateOverride?.atStartOfDay() ?: LocalDateTime.now()
    val adjustedTarget = rawTarget
      .plusHours(prefs.birthTimeZoneOffsetHours.toLong())
      .plusHours(if (prefs.compensateDst) 1L else 0L)

    val chrono = repository.calculateChronometry(active.birthDate, active.birthTime, adjustedTarget)
    val bio = repository.calculateBioMetrics(
      totalSecondsLived = chrono.totalSeconds,
      totalMinutesLived = chrono.totalMinutes,
      totalHoursLived = chrono.totalHours,
      ageYears = chrono.years,
      birthTime = active.birthTime,
      restingBpm = prefs.restingBpm,
      dailySleepHours = prefs.dailySleepHours,
      targetLifespan = prefs.targetLifespanYears
    )
    _uiState.update { it.copy(chronometry = chrono, bioMetrics = bio) }
  }

  private fun recalculateAll() {
    val state = _uiState.value
    val active = state.activeProfile ?: return
    val prefs = state.preferences
    val rawTarget = state.targetDateOverride?.atStartOfDay() ?: LocalDateTime.now()
    val targetDateTime = rawTarget
      .plusHours(prefs.birthTimeZoneOffsetHours.toLong())
      .plusHours(if (prefs.compensateDst) 1L else 0L)

    val chrono = repository.calculateChronometry(active.birthDate, active.birthTime, targetDateTime)
    val planets = repository.calculatePlanetaryAges(chrono.totalDays, targetDateTime.toLocalDate())
    val bio = repository.calculateBioMetrics(
      totalSecondsLived = chrono.totalSeconds,
      totalMinutesLived = chrono.totalMinutes,
      totalHoursLived = chrono.totalHours,
      ageYears = chrono.years,
      birthTime = active.birthTime,
      restingBpm = prefs.restingBpm,
      dailySleepHours = prefs.dailySleepHours,
      targetLifespan = prefs.targetLifespanYears
    )

    val secondary = state.profiles.find { it.id == state.secondaryProfileId }
      ?: state.profiles.firstOrNull { it.id != active.id }
    val comp = if (secondary != null) {
      repository.calculateComparison(active, secondary)
    } else null

    val cohortProfiles = state.profiles.filter { it.id in state.selectedCohortProfileIds }
    val cohortStats = repository.calculateGroupCohortStats(
      cohortProfiles.ifEmpty { state.profiles }
    )

    val currentArith = state.dateArithmetic
    val arith = repository.calculateDateArithmetic(
      baseDate = currentArith.baseDate,
      isAddition = currentArith.isAddition,
      years = currentArith.yearsOffset,
      months = currentArith.monthsOffset,
      weeks = currentArith.weeksOffset,
      days = currentArith.daysOffset
    )

    val history = repository.getHistoricalEventsForBirthYear(active.birthDate.year)

    _uiState.update {
      it.copy(
        chronometry = chrono,
        planetaryAges = planets,
        bioMetrics = bio,
        comparativeResult = comp,
        groupCohortStats = cohortStats,
        dateArithmetic = arith,
        historicalEvents = history
      )
    }
  }

  fun handleIntent(intent: ChronosUiIntent) {
    when (intent) {
      is ChronosUiIntent.SelectTab -> {
        _uiState.update { it.copy(selectedTab = intent.tab) }
      }
      is ChronosUiIntent.SelectActiveProfile -> {
        repository.setActiveProfile(intent.id)
      }
      is ChronosUiIntent.SelectSecondaryProfile -> {
        _uiState.update { it.copy(secondaryProfileId = intent.id) }
        recalculateAll()
      }
      is ChronosUiIntent.SwapComparisonProfiles -> {
        val currentActive = _uiState.value.activeProfile ?: return
        val currentSecondaryId = _uiState.value.secondaryProfileId ?: return
        _uiState.update { it.copy(secondaryProfileId = currentActive.id) }
        repository.setActiveProfile(currentSecondaryId)
      }
      is ChronosUiIntent.SetTargetDate -> {
        _uiState.update { it.copy(targetDateOverride = intent.target) }
        recalculateAll()
      }
      is ChronosUiIntent.OpenCreateProfileSheet -> {
        _uiState.update { it.copy(isProfileSheetOpen = true, profileBeingEdited = null) }
      }
      is ChronosUiIntent.OpenEditProfileSheet -> {
        _uiState.update { it.copy(isProfileSheetOpen = true, profileBeingEdited = intent.profile) }
      }
      is ChronosUiIntent.CloseProfileSheet -> {
        _uiState.update { it.copy(isProfileSheetOpen = false, profileBeingEdited = null) }
      }
      is ChronosUiIntent.SaveProfile -> {
        val profileId = intent.id ?: "profile_${UUID.randomUUID().toString().take(8)}"
        val profile = Profile(
          id = profileId,
          name = intent.name.trim().ifEmpty { "New Profile" },
          birthDate = intent.birthDate,
          birthTime = intent.birthTime,
          relationship = intent.relationship,
          avatarColorHex = intent.colorHex,
          isPrimary = intent.isPrimary
        )
        if (intent.id == null) {
          repository.addProfile(profile)
          _uiState.update { it.copy(isProfileSheetOpen = false, toastMessage = "Profile '${profile.name}' added") }
        } else {
          repository.updateProfile(profile)
          _uiState.update { it.copy(isProfileSheetOpen = false, toastMessage = "Profile '${profile.name}' updated") }
        }
        recalculateAll()
      }
      is ChronosUiIntent.DeleteProfile -> {
        val deleted = repository.deleteProfile(intent.id)
        if (deleted != null) {
          _uiState.update { it.copy(toastMessage = "Deleted profile '${deleted.name}'") }
          recalculateAll()
        }
      }
      is ChronosUiIntent.SetPrimaryProfile -> {
        val target = _uiState.value.profiles.find { it.id == intent.id } ?: return
        repository.updateProfile(target.copy(isPrimary = true))
        _uiState.update { it.copy(toastMessage = "'${target.name}' set as primary self") }
      }
      is ChronosUiIntent.AddCustomMilestone -> {
        val milestone = CustomMilestone(
          id = "ms_${UUID.randomUUID().toString().take(8)}",
          title = intent.title.trim().ifEmpty { "Custom Milestone" },
          targetDate = intent.targetDate,
          category = intent.category,
          accentColorHex = intent.colorHex
        )
        repository.addCustomMilestone(milestone)
        _uiState.update { it.copy(toastMessage = "Milestone '${milestone.title}' added") }
      }
      is ChronosUiIntent.DeleteCustomMilestone -> {
        repository.deleteCustomMilestone(intent.id)
        _uiState.update { it.copy(toastMessage = "Milestone removed") }
      }
      is ChronosUiIntent.ToggleCohortSelection -> {
        _uiState.update { state ->
          val current = state.selectedCohortProfileIds.toMutableSet()
          if (intent.profileId in current && current.size > 1) {
            current.remove(intent.profileId)
          } else {
            current.add(intent.profileId)
          }
          state.copy(selectedCohortProfileIds = current)
        }
        recalculateAll()
      }
      is ChronosUiIntent.UpdateDateArithmetic -> {
        val arith = repository.calculateDateArithmetic(
          baseDate = intent.baseDate,
          isAddition = intent.isAddition,
          years = intent.years,
          months = intent.months,
          weeks = intent.weeks,
          days = intent.days
        )
        _uiState.update { it.copy(dateArithmetic = arith) }
      }
      is ChronosUiIntent.UpdateBiometricSliders -> {
        repository.updatePreferences {
          it.copy(
            restingBpm = intent.restingBpm,
            dailySleepHours = intent.sleepHours,
            targetLifespanYears = intent.targetLifespan
          )
        }
      }
      is ChronosUiIntent.UpdateThemePreferences -> {
        repository.updatePreferences {
          it.copy(
            isDynamicColor = intent.dynamicColor,
            isAmoledNight = intent.amoledNight,
            tickerFrequencyMs = intent.tickerMs,
            useMilliseconds = intent.useMillis
          )
        }
      }
      is ChronosUiIntent.UpdateThemeMode -> {
        repository.updatePreferences {
          it.copy(
            themeMode = intent.mode,
            isAmoledNight = intent.mode == ThemeMode.AMOLED
          )
        }
      }
      is ChronosUiIntent.UpdateAccentColor -> {
        repository.updatePreferences {
          it.copy(accentColorHex = intent.colorHex, isDynamicColor = false)
        }
      }
      is ChronosUiIntent.UpdateCalendarSystem -> {
        repository.updatePreferences {
          it.copy(calendarSystem = intent.system)
        }
      }
      is ChronosUiIntent.UpdateHapticIntensity -> {
        repository.updatePreferences {
          it.copy(hapticIntensity = intent.intensity)
        }
      }
      is ChronosUiIntent.ToggleAudioChimes -> {
        repository.updatePreferences {
          it.copy(audioChimesEnabled = intent.enabled)
        }
      }
      is ChronosUiIntent.UpdateTimeZoneAndDst -> {
        repository.updatePreferences {
          it.copy(
            birthTimeZoneOffsetHours = intent.offsetHours,
            compensateDst = intent.compensateDst
          )
        }
      }
      is ChronosUiIntent.CyclePhilosophicalQuote -> {
        val quoteCount = _uiState.value.quotes.size.coerceAtLeast(1)
        repository.updatePreferences {
          it.copy(selectedQuoteIndex = (it.selectedQuoteIndex + 1) % quoteCount)
        }
      }
      is ChronosUiIntent.SelectMilestoneCardTheme -> {
        repository.updatePreferences {
          it.copy(selectedCardThemeIndex = intent.index)
        }
      }
      is ChronosUiIntent.ExportRegistryData -> {
        val content = if (intent.format.equals("JSON", ignoreCase = true)) {
          repository.exportProfilesAsJson()
        } else {
          repository.exportProfilesAsCsv()
        }
        _uiState.update {
          it.copy(exportedDataPreview = intent.format.uppercase() to content)
        }
      }
      is ChronosUiIntent.DismissExportPreview -> {
        _uiState.update { it.copy(exportedDataPreview = null) }
      }
      is ChronosUiIntent.RelaunchOnboardingTour -> {
        repository.updatePreferences { it.copy(hasCompletedOnboarding = false) }
      }
      is ChronosUiIntent.ResetSettingsToDefaults -> {
        repository.resetPreferencesToDefaults()
        _uiState.update { it.copy(toastMessage = "Preferences restored to factory defaults") }
      }
      is ChronosUiIntent.TriggerCelebration -> {
        _uiState.update { it.copy(isCelebrationActive = true) }
      }
      is ChronosUiIntent.DismissCelebration -> {
        _uiState.update { it.copy(isCelebrationActive = false) }
      }
      is ChronosUiIntent.CompleteOnboarding -> {
        repository.completeOnboarding()
      }
      is ChronosUiIntent.DismissToast -> {
        _uiState.update { it.copy(toastMessage = null) }
      }
      is ChronosUiIntent.ShowToast -> {
        _uiState.update { it.copy(toastMessage = intent.message) }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    tickerJob?.cancel()
  }
}
