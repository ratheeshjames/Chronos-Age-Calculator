package com.example.viewmodel

import com.example.data.BioMetricResult
import com.example.data.ChronometricResult
import com.example.data.ChronosPreferences
import com.example.data.ChronosTab
import com.example.data.ComparativeResult
import com.example.data.CustomMilestone
import com.example.data.DateArithmeticResult
import com.example.data.GroupCohortStats
import com.example.data.HistoricalOverlapEvent
import com.example.data.PhilosophicalQuote
import com.example.data.PlanetaryAge
import com.example.data.Profile
import java.time.LocalDate

data class ChronosUiState(
  val profiles: List<Profile> = emptyList(),
  val activeProfile: Profile? = null,
  val chronometry: ChronometricResult = ChronometricResult(),
  val planetaryAges: List<PlanetaryAge> = emptyList(),
  val bioMetrics: BioMetricResult = BioMetricResult(),
  val comparativeResult: ComparativeResult? = null,
  val customMilestones: List<CustomMilestone> = emptyList(),
  val selectedCohortProfileIds: Set<String> = emptySet(),
  val groupCohortStats: GroupCohortStats = GroupCohortStats(),
  val dateArithmetic: DateArithmeticResult = DateArithmeticResult(),
  val historicalEvents: List<HistoricalOverlapEvent> = emptyList(),
  val quotes: List<PhilosophicalQuote> = emptyList(),
  val exportedDataPreview: Pair<String, String>? = null,
  val selectedTab: ChronosTab = ChronosTab.OVERVIEW,
  val targetDateOverride: LocalDate? = null,
  val secondaryProfileId: String? = null,
  val preferences: ChronosPreferences = ChronosPreferences(),
  val isProfileSheetOpen: Boolean = false,
  val profileBeingEdited: Profile? = null,
  val isTargetDatePickerOpen: Boolean = false,
  val isCelebrationActive: Boolean = false,
  val toastMessage: String? = null
)
