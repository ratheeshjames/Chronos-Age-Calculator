package com.example.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class Relationship(val label: String) {
  SELF("Self"),
  PARTNER("Partner"),
  CHILD("Child"),
  PARENT("Parent"),
  SIBLING("Sibling"),
  FRIEND("Friend"),
  PET("Pet"),
  HISTORIC("Historic")
}

data class Profile(
  val id: String,
  val name: String,
  val birthDate: LocalDate,
  val birthTime: LocalTime? = null,
  val relationship: Relationship = Relationship.SELF,
  val avatarColorHex: Long = 0xFF1E50DE,
  val isPrimary: Boolean = false
)

data class UpcomingBirthdayForecast(
  val year: Int,
  val date: LocalDate,
  val dayOfWeek: DayOfWeek,
  val ageTurning: Int
)

data class CustomMilestone(
  val id: String,
  val title: String,
  val targetDate: LocalDate,
  val category: String = "Personal",
  val accentColorHex: Long = 0xFF2A9D8F
)

data class LunarPhaseInfo(
  val phaseName: String = "Waxing Gibbous",
  val illuminationPercent: Int = 78,
  val lunarCyclesLived: Double = 0.0,
  val nextFullMoonDays: Int = 14,
  val emoji: String = "🌔"
)

data class WorkdayBreakdown(
  val weekdayDaysLived: Long = 0L,
  val weekendDaysLived: Long = 0L,
  val weekdayPercentage: Float = 0.714f,
  val currentDayOfYear: Int = 1,
  val totalDaysInCurrentYear: Int = 365
)

data class DateArithmeticResult(
  val baseDate: LocalDate = LocalDate.now(),
  val isAddition: Boolean = true,
  val yearsOffset: Int = 0,
  val monthsOffset: Int = 0,
  val weeksOffset: Int = 0,
  val daysOffset: Int = 0,
  val resultingDate: LocalDate = LocalDate.now(),
  val resultingDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
  val totalDaysDelta: Long = 0L
)

data class HistoricalOverlapEvent(
  val title: String,
  val eventYear: Int,
  val description: String,
  val profileAgeAtEvent: Int
)

data class AchievementBadge(
  val id: String,
  val title: String,
  val subtitle: String,
  val isEarned: Boolean,
  val progressFraction: Float,
  val targetDateString: String
)

data class PhilosophicalQuote(
  val quote: String,
  val author: String,
  val era: String
)

enum class ThemeMode(val label: String) {
  SYSTEM("System"),
  LIGHT("Light"),
  DARK("Dark"),
  AMOLED("AMOLED"),
  WARM_SUNLIGHT("Warm Sun")
}

enum class CalendarSystem(val label: String) {
  GREGORIAN("Gregorian Standard"),
  ISO_8601("ISO-8601 Week"),
  JULIAN("Julian Astronomical")
}

enum class HapticIntensity(val label: String) {
  OFF("Off"),
  LIGHT("Light"),
  CRISP("Crisp")
}

data class ChronometricResult(
  val years: Int = 0,
  val months: Int = 0,
  val days: Int = 0,
  val hours: Int = 0,
  val minutes: Int = 0,
  val seconds: Int = 0,
  val millis: Int = 0,
  val totalDays: Long = 0L,
  val totalWeeks: Long = 0L,
  val totalHours: Long = 0L,
  val totalMinutes: Long = 0L,
  val totalSeconds: Long = 0L,
  val leapDaysLived: Int = 0,
  val dayOfWeekBorn: DayOfWeek = DayOfWeek.MONDAY,
  val nextBirthdayDate: LocalDate = LocalDate.now(),
  val nextBirthdayDaysRemaining: Long = 0L,
  val nextBirthdayHoursRemaining: Long = 0L,
  val nextBirthdayMinutesRemaining: Long = 0L,
  val nextBirthdaySecondsRemaining: Long = 0L,
  val halfBirthdayDate: LocalDate = LocalDate.now(),
  val daysUntilHalfBirthday: Long = 0L,
  val goldenBirthdayYear: Int = 2030,
  val goldenBirthdayAge: Int = 1,
  val isGoldenBirthdayPast: Boolean = false,
  val tenThousandthDayDate: LocalDate = LocalDate.now(),
  val daysUntilTenThousandthDay: Long = 0L,
  val billionthSecondDate: LocalDateTime = LocalDateTime.now(),
  val secondsUntilBillionth: Long = 0L,
  val yearProgressFraction: Float = 0f,
  val upcomingBirthdays: List<UpcomingBirthdayForecast> = emptyList(),
  val nextDecadeAge: Int = 30,
  val daysUntilNextDecade: Long = 0L,
  val decadeProgressFraction: Float = 0f,
  val westernZodiac: String = "Taurus",
  val lunarZodiac: String = "Rat",
  val birthstone: String = "Emerald",
  val birthFlower: String = "Lily of the Valley",
  val dayOfWeekTrivia: String = "Fair of face",
  val lunarPhaseAtBirth: LunarPhaseInfo = LunarPhaseInfo(),
  val workdayBreakdown: WorkdayBreakdown = WorkdayBreakdown(),
  val julianDayNumberBirth: Long = 2450218L,
  val julianDayNumberCurrent: Long = 2461342L,
  val isoWeekDateString: String = "2026-W44-3",
  val julianCalendarDateString: String = "1996-05-01 (Julian)",
  val achievementBadges: List<AchievementBadge> = emptyList()
)

enum class Planet(
  val displayName: String,
  val orbitalDays: Double,
  val colorHex: Long,
  val distanceAu: Double,
  val gravityFactor: Double,
  val rotationHours: Double,
  val tagline: String
) {
  MERCURY("Mercury", 87.969, 0xFF9E9E9E, 0.39, 0.38, 1407.6, "Swift Innermost World"),
  VENUS("Venus", 224.701, 0xFFE0A96D, 0.72, 0.90, 5832.5, "Morning & Evening Star"),
  EARTH("Earth", 365.256, 0xFF1E50DE, 1.00, 1.00, 23.93, "Home Blue Marble"),
  MARS("Mars", 686.980, 0xFFE76F51, 1.52, 0.38, 24.62, "The Red Frontier"),
  JUPITER("Jupiter", 4332.589, 0xFFDDA15E, 5.20, 2.53, 9.93, "Gas Giant Sovereign"),
  SATURN("Saturn", 10759.22, 0xFFC9A227, 9.58, 1.07, 10.7, "Jewel of the Ring System"),
  URANUS("Uranus", 30685.4, 0xFF48CAE4, 19.20, 0.89, 17.24, "Sideways Ice Giant"),
  NEPTUNE("Neptune", 60189.0, 0xFF0077B6, 30.05, 1.14, 16.11, "Supersonic Azure World"),
  PLUTO("Pluto", 90560.0, 0xFFADB5BD, 39.48, 0.06, 153.3, "Kuiper Belt Dwarf")
}

data class PlanetaryAge(
  val planet: Planet,
  val ageInPlanetaryYears: Double,
  val orbitProgressFraction: Float,
  val nextBirthdayDays: Long,
  val nextBirthdayDate: LocalDate,
  val completedOrbits: Int,
  val planetaryDaysLived: Double
)

data class BioMetricResult(
  val estimatedHeartbeats: Long = 0L,
  val estimatedBreaths: Long = 0L,
  val estimatedBlinks: Long = 0L,
  val sleepHoursAccumulated: Long = 0L,
  val sleepYearsEquivalent: Float = 0f,
  val remSleepHours: Long = 0L,
  val deepSleepHours: Long = 0L,
  val lightSleepHours: Long = 0L,
  val circadianChronotype: String = "Solar Hummingbird",
  val circadianPeakWindow: String = "10:00 AM – 2:00 PM",
  val lifeStageName: String = "Adult",
  val lifeStageDescription: String = "Peak cognitive synthesis and vitality",
  val lifePercentage: Float = 0.35f,
  val seasonsLived: Map<String, Int> = emptyMap()
)

data class ComparativeResult(
  val profileA: Profile,
  val profileB: Profile,
  val yearsDifference: Int = 0,
  val monthsDifference: Int = 0,
  val daysDifference: Int = 0,
  val totalDaysDifference: Long = 0L,
  val totalHoursDifference: Long = 0L,
  val combinedTotalYears: Int = 0,
  val combinedTotalDays: Long = 0L,
  val ageRatio: Double = 1.0,
  val doubleAgeConvergenceDate: LocalDate? = null,
  val sesquiAgeConvergenceDate: LocalDate? = null,
  val olderProfileName: String = "",
  val youngerProfileName: String = ""
)

data class GroupCohortStats(
  val selectedCount: Int = 0,
  val averageAgeYears: Double = 0.0,
  val medianAgeYears: Double = 0.0,
  val totalCombinedDays: Long = 0L,
  val oldestMemberName: String = "",
  val youngestMemberName: String = "",
  val generationSpanYears: Int = 0
)

enum class ChronosTab(val title: String) {
  OVERVIEW("Live Age"),
  PROFILES("Profiles"),
  PLANETARY("Cosmos"),
  BIO_DIFF("Bio & Tools"),
  ANALYTICS("Life Grid")
}

data class ChronosPreferences(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val isDynamicColor: Boolean = false,
  val isAmoledNight: Boolean = false,
  val accentColorHex: Long = 0xFF1E50DE,
  val tickerFrequencyMs: Long = 1000L,
  val useMilliseconds: Boolean = false,
  val calendarSystem: CalendarSystem = CalendarSystem.GREGORIAN,
  val hapticIntensity: HapticIntensity = HapticIntensity.CRISP,
  val audioChimesEnabled: Boolean = true,
  val birthTimeZoneOffsetHours: Int = 0,
  val compensateDst: Boolean = true,
  val selectedQuoteIndex: Int = 0,
  val selectedCardThemeIndex: Int = 0,
  val targetLifespanYears: Int = 80,
  val restingBpm: Int = 72,
  val dailySleepHours: Float = 8.0f,
  val hasCompletedOnboarding: Boolean = false
)
