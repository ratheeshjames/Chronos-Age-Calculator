package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.JulianFields
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.roundToInt

class ChronosRepository {

  private val initialProfiles = listOf(
    Profile(
      id = "profile_self",
      name = "Alex Vance",
      birthDate = LocalDate.of(1996, 5, 14),
      birthTime = LocalTime.of(8, 30),
      relationship = Relationship.SELF,
      avatarColorHex = 0xFF1E50DE,
      isPrimary = true
    ),
    Profile(
      id = "profile_partner",
      name = "Elena Rostova",
      birthDate = LocalDate.of(1998, 9, 22),
      birthTime = LocalTime.of(14, 15),
      relationship = Relationship.PARTNER,
      avatarColorHex = 0xFF7044BA,
      isPrimary = false
    ),
    Profile(
      id = "profile_child",
      name = "Leo Vance",
      birthDate = LocalDate.of(2023, 11, 3),
      birthTime = LocalTime.of(6, 45),
      relationship = Relationship.CHILD,
      avatarColorHex = 0xFF2A9D8F,
      isPrimary = false
    ),
    Profile(
      id = "profile_einstein",
      name = "Albert Einstein",
      birthDate = LocalDate.of(1879, 3, 14),
      birthTime = LocalTime.of(11, 30),
      relationship = Relationship.HISTORIC,
      avatarColorHex = 0xFFFFB703,
      isPrimary = false
    ),
    Profile(
      id = "profile_lovelace",
      name = "Ada Lovelace",
      birthDate = LocalDate.of(1815, 12, 10),
      birthTime = LocalTime.of(13, 0),
      relationship = Relationship.HISTORIC,
      avatarColorHex = 0xFFB5236A,
      isPrimary = false
    )
  )

  private val initialMilestones = listOf(
    CustomMilestone(
      id = "ms_sabbatical",
      title = "Global Sabbatical Expedition",
      targetDate = LocalDate.now().plusDays(180),
      category = "Adventure",
      accentColorHex = 0xFF1E50DE
    ),
    CustomMilestone(
      id = "ms_anniversary",
      title = "500-Week Career Milestone",
      targetDate = LocalDate.now().plusDays(42),
      category = "Career",
      accentColorHex = 0xFF2A9D8F
    ),
    CustomMilestone(
      id = "ms_eclipse",
      title = "Next Total Solar Eclipse",
      targetDate = LocalDate.of(2027, 8, 2),
      category = "Astronomy",
      accentColorHex = 0xFFFFB703
    )
  )

  private val _profiles = MutableStateFlow(initialProfiles)
  val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

  private val _activeProfileId = MutableStateFlow("profile_self")
  val activeProfileId: StateFlow<String> = _activeProfileId.asStateFlow()

  private val _customMilestones = MutableStateFlow(initialMilestones)
  val customMilestones: StateFlow<List<CustomMilestone>> = _customMilestones.asStateFlow()

  private val _preferences = MutableStateFlow(ChronosPreferences())
  val preferences: StateFlow<ChronosPreferences> = _preferences.asStateFlow()

  fun setActiveProfile(id: String) {
    if (_profiles.value.any { it.id == id }) {
      _activeProfileId.value = id
    }
  }

  fun addProfile(profile: Profile) {
    val updated = _profiles.value.toMutableList()
    if (profile.isPrimary) {
      for (i in updated.indices) {
        if (updated[i].isPrimary) updated[i] = updated[i].copy(isPrimary = false)
      }
    }
    updated.add(profile)
    _profiles.value = updated
    _activeProfileId.value = profile.id
  }

  fun updateProfile(profile: Profile) {
    val updated = _profiles.value.map {
      if (it.id == profile.id) profile else {
        if (profile.isPrimary && it.isPrimary) it.copy(isPrimary = false) else it
      }
    }
    _profiles.value = updated
  }

  fun deleteProfile(id: String): Profile? {
    val existing = _profiles.value.find { it.id == id } ?: return null
    if (_profiles.value.size <= 1) return null
    val updated = _profiles.value.filter { it.id != id }
    _profiles.value = updated
    if (_activeProfileId.value == id) {
      _activeProfileId.value = updated.first().id
    }
    return existing
  }

  fun addCustomMilestone(milestone: CustomMilestone) {
    _customMilestones.value = _customMilestones.value + milestone
  }

  fun deleteCustomMilestone(id: String) {
    _customMilestones.value = _customMilestones.value.filter { it.id != id }
  }

  fun updatePreferences(transform: (ChronosPreferences) -> ChronosPreferences) {
    _preferences.value = transform(_preferences.value)
  }

  fun completeOnboarding() {
    _preferences.value = _preferences.value.copy(hasCompletedOnboarding = true)
  }

  fun resetPreferencesToDefaults() {
    _preferences.value = ChronosPreferences(hasCompletedOnboarding = true)
  }

  // --- Computational Engines ---

  fun calculateChronometry(
    birthDate: LocalDate,
    birthTime: LocalTime?,
    targetDateTime: LocalDateTime = LocalDateTime.now()
  ): ChronometricResult {
    val actualBirthTime = birthTime ?: LocalTime.MIDNIGHT
    val birthDateTime = LocalDateTime.of(birthDate, actualBirthTime)

    val validTarget = if (targetDateTime.isBefore(birthDateTime)) birthDateTime else targetDateTime

    val targetDate = validTarget.toLocalDate()
    val period = Period.between(birthDate, targetDate)

    // Sub-day calculation
    val durationTotal = Duration.between(birthDateTime, validTarget)
    val totalSeconds = durationTotal.seconds.coerceAtLeast(0L)
    val totalMinutes = totalSeconds / 60
    val totalHours = totalSeconds / 3600
    val totalDays = ChronoUnit.DAYS.between(birthDate, targetDate).coerceAtLeast(0L)
    val totalWeeks = totalDays / 7

    val hoursPart = ((totalSeconds % 86400) / 3600).toInt()
    val minutesPart = ((totalSeconds % 3600) / 60).toInt()
    val secondsPart = (totalSeconds % 60).toInt()
    val millisPart = (validTarget.nano / 1_000_000)

    // Leap-years calculation
    var leapDays = 0
    for (year in birthDate.year..targetDate.year) {
      if (java.time.Year.isLeap(year.toLong())) {
        val leapDay = LocalDate.of(year, 2, 29)
        if (!leapDay.isBefore(birthDate) && !leapDay.isAfter(targetDate)) {
          leapDays++
        }
      }
    }

    // Next birthday
    var nextBirthday = birthDate.withYear(targetDate.year)
    if (nextBirthday.isBefore(targetDate) || nextBirthday.isEqual(targetDate)) {
      nextBirthday = birthDate.withYear(targetDate.year + 1)
    }
    val nextBirthdayDateTime = LocalDateTime.of(nextBirthday, actualBirthTime)
    val nextDuration = Duration.between(validTarget, nextBirthdayDateTime).coerceAtLeast(Duration.ZERO)

    val nextDays = nextDuration.toDays()
    val nextHours = (nextDuration.toHours() % 24)
    val nextMinutes = (nextDuration.toMinutes() % 60)
    val nextSeconds = (nextDuration.seconds % 60)

    // Milestones
    var upcomingHalfBirthday = birthDate.plusMonths(6).withYear(targetDate.year)
    if (upcomingHalfBirthday.isBefore(targetDate)) {
      upcomingHalfBirthday = upcomingHalfBirthday.plusYears(1)
    }
    val daysUntilHalf = ChronoUnit.DAYS.between(targetDate, upcomingHalfBirthday).coerceAtLeast(0L)

    val goldenAge = birthDate.dayOfMonth
    val goldenYear = birthDate.year + goldenAge
    val isGoldenPast = targetDate.year > goldenYear || (targetDate.year == goldenYear && targetDate.isAfter(birthDate.withYear(goldenYear)))

    val tenThousandthDay = birthDate.plusDays(10_000L)
    val daysUntilTenThousandth = ChronoUnit.DAYS.between(targetDate, tenThousandthDay)

    val billionthSecond = birthDateTime.plusSeconds(1_000_000_000L)
    val secondsUntilBillionth = Duration.between(validTarget, billionthSecond).seconds

    val daysInThisAgeYear = ChronoUnit.DAYS.between(
      birthDate.withYear(nextBirthday.year - 1),
      nextBirthday
    ).coerceAtLeast(1L)
    val daysElapsedInCurrentAgeYear = daysInThisAgeYear - nextDays
    val fraction = (daysElapsedInCurrentAgeYear.toFloat() / daysInThisAgeYear.toFloat()).coerceIn(0f, 1f)

    // Upcoming 5 birthdays forecast
    val upcomingBirthdays = (0..4).map { offset ->
      val bdayYear = nextBirthday.year + offset
      val bdayDate = birthDate.withYear(bdayYear)
      UpcomingBirthdayForecast(
        year = bdayYear,
        date = bdayDate,
        dayOfWeek = bdayDate.dayOfWeek,
        ageTurning = bdayYear - birthDate.year
      )
    }

    // Decade progress
    val nextDecade = ((period.years / 10) + 1) * 10
    val nextDecadeDate = birthDate.plusYears(nextDecade.toLong())
    val prevDecadeDate = birthDate.plusYears((nextDecade - 10).toLong())
    val daysInDecade = ChronoUnit.DAYS.between(prevDecadeDate, nextDecadeDate).coerceAtLeast(1L)
    val daysIntoDecade = ChronoUnit.DAYS.between(prevDecadeDate, targetDate).coerceAtLeast(0L)
    val decadeFraction = (daysIntoDecade.toFloat() / daysInDecade.toFloat()).coerceIn(0f, 1f)
    val daysUntilDecade = ChronoUnit.DAYS.between(targetDate, nextDecadeDate).coerceAtLeast(0L)

    // Lunar Phase at Birth & Synodic Cycles
    val lunarInfo = calculateLunarPhase(birthDate, totalDays)

    // Workday vs Weekend Breakdown
    val workdayInfo = calculateWorkdayBreakdown(birthDate, targetDate, totalDays)

    // Astronomical Julian Day Numbers & ISO-8601 / Julian Calendar Dates
    val jdnBirth = birthDate.getLong(JulianFields.JULIAN_DAY)
    val jdnCurrent = targetDate.getLong(JulianFields.JULIAN_DAY)
    val isoWeekStr = targetDate.format(DateTimeFormatter.ISO_WEEK_DATE)
    val julianBirthDate = birthDate.minusDays(13)
    val julianStr = "$julianBirthDate (Julian O.S.)"

    // Achievement Badges (10,000 Days, 100k Hours, 250k Hours, 500k Hours, 1M Hours, 1B Seconds)
    val badges = listOf(
      AchievementBadge(
        id = "badge_100k_hours",
        title = "100,000-Hour Voyager",
        subtitle = "11.41 Earth Years",
        isEarned = totalHours >= 100_000L,
        progressFraction = (totalHours.toFloat() / 100_000f).coerceIn(0f, 1f),
        targetDateString = birthDateTime.plusHours(100_000L).toLocalDate().toString()
      ),
      AchievementBadge(
        id = "badge_10k_days",
        title = "10,000-Day Sovereign",
        subtitle = "Deca-Millennium Day Mark",
        isEarned = totalDays >= 10_000L,
        progressFraction = (totalDays.toFloat() / 10_000f).coerceIn(0f, 1f),
        targetDateString = tenThousandthDay.toString()
      ),
      AchievementBadge(
        id = "badge_250k_hours",
        title = "Quarter-Million Hours",
        subtitle = "250,000 Hours Lived",
        isEarned = totalHours >= 250_000L,
        progressFraction = (totalHours.toFloat() / 250_000f).coerceIn(0f, 1f),
        targetDateString = birthDateTime.plusHours(250_000L).toLocalDate().toString()
      ),
      AchievementBadge(
        id = "badge_1b_seconds",
        title = "Gigasecond Luminary",
        subtitle = "1,000,000,000 Seconds",
        isEarned = totalSeconds >= 1_000_000_000L,
        progressFraction = (totalSeconds.toFloat() / 1_000_000_000f).coerceIn(0f, 1f),
        targetDateString = billionthSecond.toLocalDate().toString()
      ),
      AchievementBadge(
        id = "badge_500k_hours",
        title = "Half-Million Hour Sage",
        subtitle = "500,000 Hours (~57.0 yrs)",
        isEarned = totalHours >= 500_000L,
        progressFraction = (totalHours.toFloat() / 500_000f).coerceIn(0f, 1f),
        targetDateString = birthDateTime.plusHours(500_000L).toLocalDate().toString()
      ),
      AchievementBadge(
        id = "badge_1m_hours",
        title = "1,000,000-Hour Immortal",
        subtitle = "Mega-Hour Threshold (~114.1 yrs)",
        isEarned = totalHours >= 1_000_000L,
        progressFraction = (totalHours.toFloat() / 1_000_000f).coerceIn(0f, 1f),
        targetDateString = birthDateTime.plusHours(1_000_000L).toLocalDate().toString()
      )
    )

    return ChronometricResult(
      years = period.years,
      months = period.months,
      days = period.days,
      hours = hoursPart,
      minutes = minutesPart,
      seconds = secondsPart,
      millis = millisPart,
      totalDays = totalDays,
      totalWeeks = totalWeeks,
      totalHours = totalHours,
      totalMinutes = totalMinutes,
      totalSeconds = totalSeconds,
      leapDaysLived = leapDays,
      dayOfWeekBorn = birthDate.dayOfWeek,
      nextBirthdayDate = nextBirthday,
      nextBirthdayDaysRemaining = nextDays,
      nextBirthdayHoursRemaining = nextHours,
      nextBirthdayMinutesRemaining = nextMinutes,
      nextBirthdaySecondsRemaining = nextSeconds,
      halfBirthdayDate = upcomingHalfBirthday,
      daysUntilHalfBirthday = daysUntilHalf,
      goldenBirthdayYear = goldenYear,
      goldenBirthdayAge = goldenAge,
      isGoldenBirthdayPast = isGoldenPast,
      tenThousandthDayDate = tenThousandthDay,
      daysUntilTenThousandthDay = daysUntilTenThousandth,
      billionthSecondDate = billionthSecond,
      secondsUntilBillionth = secondsUntilBillionth,
      yearProgressFraction = fraction,
      upcomingBirthdays = upcomingBirthdays,
      nextDecadeAge = nextDecade,
      daysUntilNextDecade = daysUntilDecade,
      decadeProgressFraction = decadeFraction,
      westernZodiac = getWesternZodiac(birthDate.monthValue, birthDate.dayOfMonth),
      lunarZodiac = getLunarZodiac(birthDate.year),
      birthstone = getBirthstone(birthDate.monthValue),
      birthFlower = getBirthFlower(birthDate.monthValue),
      dayOfWeekTrivia = getDayOfWeekTrivia(birthDate.dayOfWeek),
      lunarPhaseAtBirth = lunarInfo,
      workdayBreakdown = workdayInfo,
      julianDayNumberBirth = jdnBirth,
      julianDayNumberCurrent = jdnCurrent,
      isoWeekDateString = isoWeekStr,
      julianCalendarDateString = julianStr,
      achievementBadges = badges
    )
  }

  private fun calculateLunarPhase(birthDate: LocalDate, totalDaysLived: Long): LunarPhaseInfo {
    val synodicMonth = 29.530588853
    // Known new moon reference: 2000-01-06
    val refNewMoon = LocalDate.of(2000, 1, 6)
    val daysSinceRefAtBirth = ChronoUnit.DAYS.between(refNewMoon, birthDate).toDouble()
    var phaseFraction = (daysSinceRefAtBirth % synodicMonth) / synodicMonth
    if (phaseFraction < 0) phaseFraction += 1.0

    val illumination = ((1.0 - cos(phaseFraction * 2.0 * PI)) / 2.0 * 100.0).roundToInt().coerceIn(0, 100)

    val (name, emoji) = when {
      phaseFraction < 0.0625 || phaseFraction >= 0.9375 -> "New Moon" to "🌑"
      phaseFraction < 0.1875 -> "Waxing Crescent" to "🌒"
      phaseFraction < 0.3125 -> "First Quarter" to "🌓"
      phaseFraction < 0.4375 -> "Waxing Gibbous" to "🌔"
      phaseFraction < 0.5625 -> "Full Moon" to "🌕"
      phaseFraction < 0.6875 -> "Waning Gibbous" to "🌖"
      phaseFraction < 0.8125 -> "Last Quarter" to "🌗"
      else -> "Waning Crescent" to "🌘"
    }

    val cyclesLived = totalDaysLived.toDouble() / synodicMonth
    val daysSinceRefToday = ChronoUnit.DAYS.between(refNewMoon, LocalDate.now()).toDouble()
    var currentPhase = (daysSinceRefToday % synodicMonth) / synodicMonth
    if (currentPhase < 0) currentPhase += 1.0
    val fractionToFull = if (currentPhase <= 0.5) (0.5 - currentPhase) else (1.5 - currentPhase)
    val nextFullDays = (fractionToFull * synodicMonth).roundToInt()

    return LunarPhaseInfo(
      phaseName = name,
      illuminationPercent = illumination,
      lunarCyclesLived = cyclesLived,
      nextFullMoonDays = nextFullDays,
      emoji = emoji
    )
  }

  private fun calculateWorkdayBreakdown(
    birthDate: LocalDate,
    targetDate: LocalDate,
    totalDays: Long
  ): WorkdayBreakdown {
    val fullWeeks = totalDays / 7L
    val remDays = (totalDays % 7L).toInt()
    var weekdays = fullWeeks * 5L
    var weekends = fullWeeks * 2L

    var cursor = birthDate
    repeat(remDays) {
      val dow = cursor.dayOfWeek
      if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
        weekends++
      } else {
        weekdays++
      }
      cursor = cursor.plusDays(1)
    }

    val pct = if (totalDays > 0) (weekdays.toFloat() / totalDays.toFloat()) else 0.714f
    val dayOfYear = targetDate.dayOfYear
    val totalDaysInYear = if (targetDate.isLeapYear) 366 else 365

    return WorkdayBreakdown(
      weekdayDaysLived = weekdays,
      weekendDaysLived = weekends,
      weekdayPercentage = pct.coerceIn(0f, 1f),
      currentDayOfYear = dayOfYear,
      totalDaysInCurrentYear = totalDaysInYear
    )
  }

  private fun getWesternZodiac(month: Int, day: Int): String = when {
    (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Aries ♈"
    (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Taurus ♉"
    (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Gemini ♊"
    (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Cancer ♋"
    (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Leo ♌"
    (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Virgo ♍"
    (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Libra ♎"
    (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Scorpio ♏"
    (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Sagittarius ♐"
    (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Capricorn ♑"
    (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Aquarius ♒"
    else -> "Pisces ♓"
  }

  private fun getLunarZodiac(year: Int): String {
    val animals = listOf("Rat 🐀", "Ox 🐂", "Tiger 🐅", "Rabbit 🐇", "Dragon 🐉", "Snake 🐍", "Horse 🐎", "Goat 🐐", "Monkey 🐒", "Rooster 🐓", "Dog 🐕", "Pig 🐖")
    val index = (year - 4) % 12
    return animals[if (index >= 0) index else index + 12]
  }

  private fun getBirthstone(month: Int): String = when (month) {
    1 -> "Garnet"
    2 -> "Amethyst"
    3 -> "Aquamarine"
    4 -> "Diamond"
    5 -> "Emerald"
    6 -> "Pearl / Alexandrite"
    7 -> "Ruby"
    8 -> "Peridot"
    9 -> "Sapphire"
    10 -> "Opal / Tourmaline"
    11 -> "Topaz / Citrine"
    else -> "Tanzanite / Turquoise"
  }

  private fun getBirthFlower(month: Int): String = when (month) {
    1 -> "Carnation & Snowdrop"
    2 -> "Violet & Primrose"
    3 -> "Daffodil"
    4 -> "Daisy & Sweet Pea"
    5 -> "Lily of the Valley & Hawthorn"
    6 -> "Rose & Honeysuckle"
    7 -> "Larkspur & Water Lily"
    8 -> "Gladiolus & Poppy"
    9 -> "Aster & Morning Glory"
    10 -> "Marigold & Cosmos"
    11 -> "Chrysanthemum"
    else -> "Narcissus & Holly"
  }

  private fun getDayOfWeekTrivia(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "Monday's child is fair of face"
    DayOfWeek.TUESDAY -> "Tuesday's child is full of grace"
    DayOfWeek.WEDNESDAY -> "Wednesday's child is full of woe"
    DayOfWeek.THURSDAY -> "Thursday's child has far to go"
    DayOfWeek.FRIDAY -> "Friday's child is loving and giving"
    DayOfWeek.SATURDAY -> "Saturday's child works hard for a living"
    DayOfWeek.SUNDAY -> "Sunday's child is bonny and blithe, good and gay"
  }

  fun calculatePlanetaryAges(totalDaysLived: Long, targetDate: LocalDate = LocalDate.now()): List<PlanetaryAge> {
    val earthDays = totalDaysLived.toDouble().coerceAtLeast(0.0)
    return Planet.entries.map { planet ->
      val planetYears = earthDays / planet.orbitalDays
      val completedOrbits = planetYears.toInt()
      val orbitProgress = (planetYears - completedOrbits).toFloat().coerceIn(0f, 1f)
      val daysSinceLastPlanetYear = earthDays % planet.orbitalDays
      val daysUntilNextPlanetYear = (planet.orbitalDays - daysSinceLastPlanetYear).toLong().coerceAtLeast(0L)
      val nextBdayDate = targetDate.plusDays(daysUntilNextPlanetYear)
      val planetDaysLived = (earthDays * 24.0) / planet.rotationHours

      PlanetaryAge(
        planet = planet,
        ageInPlanetaryYears = planetYears,
        orbitProgressFraction = orbitProgress,
        nextBirthdayDays = daysUntilNextPlanetYear,
        nextBirthdayDate = nextBdayDate,
        completedOrbits = completedOrbits,
        planetaryDaysLived = planetDaysLived
      )
    }
  }

  fun calculateBioMetrics(
    totalSecondsLived: Long,
    totalMinutesLived: Long,
    totalHoursLived: Long,
    ageYears: Int,
    birthTime: LocalTime?,
    restingBpm: Int,
    dailySleepHours: Float,
    targetLifespan: Int
  ): BioMetricResult {
    // Live second-level heartbeat & breath ticker
    val heartbeats = ((totalSecondsLived.toDouble() / 60.0) * restingBpm.toDouble()).toLong()
    val breaths = ((totalSecondsLived.toDouble() / 60.0) * 15.0).toLong()
    val wakingRatio = ((24f - dailySleepHours) / 24f).coerceIn(0.2f, 0.9f)
    val blinks = ((totalMinutesLived * wakingRatio) * 17f).toLong()

    val sleepHours = (totalHoursLived * (dailySleepHours / 24f)).toLong()
    val sleepYears = sleepHours / 8760f

    // Sleep stages: ~22% REM, ~20% Deep, ~58% Light
    val remHours = (sleepHours * 0.22f).toLong()
    val deepHours = (sleepHours * 0.20f).toLong()
    val lightHours = (sleepHours - remHours - deepHours).coerceAtLeast(0L)

    val birthHour = birthTime?.hour ?: 9
    val (chronotype, peakWindow) = when (birthHour) {
      in 4..9 -> "Morning Lark 🌅" to "08:00 AM – 12:30 PM"
      in 10..16 -> "Solar Hummingbird ☀️" to "10:30 AM – 03:00 PM"
      else -> "Nocturnal Owl 🌙" to "04:00 PM – 10:30 PM"
    }

    val (stage, desc) = when {
      ageYears < 2 -> "Infancy" to "Rapid neurogenesis and sensory discovery"
      ageYears < 12 -> "Childhood" to "Foundational motor skill & language expansion"
      ageYears < 18 -> "Adolescence" to "Dynamic biological growth and identity formation"
      ageYears < 35 -> "Early Adulthood" to "Peak physical vitality and cognitive speed"
      ageYears < 60 -> "Middle Adulthood" to "High executive synthesis, mastery, and resilience"
      else -> "Golden Wisdom" to "Deep crystallized intelligence and legacy stewardship"
    }

    val percentage = (ageYears.toFloat() / targetLifespan.toFloat()).coerceIn(0f, 1f)

    val seasons = mapOf(
      "Spring" to ageYears,
      "Summer" to ageYears,
      "Autumn" to ageYears,
      "Winter" to ageYears
    )

    return BioMetricResult(
      estimatedHeartbeats = heartbeats,
      estimatedBreaths = breaths,
      estimatedBlinks = blinks,
      sleepHoursAccumulated = sleepHours,
      sleepYearsEquivalent = sleepYears,
      remSleepHours = remHours,
      deepSleepHours = deepHours,
      lightSleepHours = lightHours,
      circadianChronotype = chronotype,
      circadianPeakWindow = peakWindow,
      lifeStageName = stage,
      lifeStageDescription = desc,
      lifePercentage = percentage,
      seasonsLived = seasons
    )
  }

  fun calculateComparison(profileA: Profile, profileB: Profile): ComparativeResult {
    val dateA = profileA.birthDate
    val dateB = profileB.birthDate

    val (older, younger) = if (dateA.isBefore(dateB) || dateA.isEqual(dateB)) {
      profileA to profileB
    } else {
      profileB to profileA
    }

    val period = Period.between(older.birthDate, younger.birthDate)
    val totalDaysDiff = ChronoUnit.DAYS.between(older.birthDate, younger.birthDate).coerceAtLeast(0L)
    val totalHoursDiff = totalDaysDiff * 24L

    val now = LocalDate.now()
    val daysOlder = ChronoUnit.DAYS.between(older.birthDate, now).coerceAtLeast(1L)
    val daysYounger = ChronoUnit.DAYS.between(younger.birthDate, now).coerceAtLeast(1L)
    val ratio = daysOlder.toDouble() / daysYounger.toDouble()

    val combinedDays = (daysOlder + daysYounger).coerceAtLeast(0L)
    val combinedYears = (combinedDays / 365.25).toInt()

    // Double age convergence: older_age = 2 * younger_age => younger.birthDate + totalDaysDiff
    val doubleAgeDate = younger.birthDate.plusDays(totalDaysDiff)
    // 1.5x age convergence: older_age = 1.5 * younger_age => younger.birthDate + 2 * totalDaysDiff
    val sesquiAgeDate = younger.birthDate.plusDays(totalDaysDiff * 2L)

    return ComparativeResult(
      profileA = profileA,
      profileB = profileB,
      yearsDifference = period.years,
      monthsDifference = period.months,
      daysDifference = period.days,
      totalDaysDifference = totalDaysDiff,
      totalHoursDifference = totalHoursDiff,
      combinedTotalYears = combinedYears,
      combinedTotalDays = combinedDays,
      ageRatio = ratio,
      doubleAgeConvergenceDate = doubleAgeDate,
      sesquiAgeConvergenceDate = sesquiAgeDate,
      olderProfileName = older.name,
      youngerProfileName = younger.name
    )
  }

  fun calculateGroupCohortStats(selectedProfiles: List<Profile>): GroupCohortStats {
    if (selectedProfiles.isEmpty()) return GroupCohortStats()
    val now = LocalDate.now()
    val sortedByDob = selectedProfiles.sortedBy { it.birthDate }
    val oldest = sortedByDob.first()
    val youngest = sortedByDob.last()

    val agesInDays = selectedProfiles.map {
      ChronoUnit.DAYS.between(it.birthDate, now).coerceAtLeast(0L)
    }.sorted()

    val totalDaysSum = agesInDays.sum()
    val avgYears = (totalDaysSum.toDouble() / selectedProfiles.size.toDouble()) / 365.25

    val medianDays = if (agesInDays.size % 2 == 1) {
      agesInDays[agesInDays.size / 2].toDouble()
    } else {
      val mid = agesInDays.size / 2
      (agesInDays[mid - 1] + agesInDays[mid]) / 2.0
    }
    val medianYears = medianDays / 365.25
    val spanYears = Period.between(oldest.birthDate, youngest.birthDate).years

    return GroupCohortStats(
      selectedCount = selectedProfiles.size,
      averageAgeYears = avgYears,
      medianAgeYears = medianYears,
      totalCombinedDays = totalDaysSum,
      oldestMemberName = oldest.name,
      youngestMemberName = youngest.name,
      generationSpanYears = spanYears
    )
  }

  fun calculateDateArithmetic(
    baseDate: LocalDate,
    isAddition: Boolean,
    years: Int,
    months: Int,
    weeks: Int,
    days: Int
  ): DateArithmeticResult {
    val result = if (isAddition) {
      baseDate
        .plusYears(years.toLong())
        .plusMonths(months.toLong())
        .plusWeeks(weeks.toLong())
        .plusDays(days.toLong())
    } else {
      baseDate
        .minusYears(years.toLong())
        .minusMonths(months.toLong())
        .minusWeeks(weeks.toLong())
        .minusDays(days.toLong())
    }
    val deltaDays = ChronoUnit.DAYS.between(baseDate, result)
    return DateArithmeticResult(
      baseDate = baseDate,
      isAddition = isAddition,
      yearsOffset = years,
      monthsOffset = months,
      weeksOffset = weeks,
      daysOffset = days,
      resultingDate = result,
      resultingDayOfWeek = result.dayOfWeek,
      totalDaysDelta = deltaDays
    )
  }

  fun getHistoricalEventsForBirthYear(birthYear: Int): List<HistoricalOverlapEvent> {
    val milestones = listOf(
      Triple(1969, "Apollo 11 Lunar Landing", "Humanity first walked on the Moon at Sea of Tranquility."),
      Triple(1989, "World Wide Web Invented", "Tim Berners-Lee proposed the global hypertext system at CERN."),
      Triple(1990, "Hubble Space Telescope Launch", "Deployed into low Earth orbit, transforming deep-space astronomy."),
      Triple(2000, "Millennium Turn & ISS Crew", "Expedition 1 permanently crewed the International Space Station."),
      Triple(2007, "Modern Smartphone Era", "Mobile computing transformed daily human connection."),
      Triple(2012, "Curiosity Rover on Mars & Higgs Boson", "CERN confirmed the Higgs Boson; Curiosity landed in Gale Crater."),
      Triple(2015, "First Gravitational Waves Detected", "LIGO observed ripples in spacetime from colliding black holes."),
      Triple(2021, "James Webb Space Telescope Launch", "Infrared observatory launched to Lagrange Point L2."),
      Triple(2024, "Commercial Lunar Landings", "New era of Artemis & private lunar surface exploration.")
    )
    return milestones.mapNotNull { (yr, title, desc) ->
      val ageAtEvent = yr - birthYear
      if (ageAtEvent >= 0) {
        HistoricalOverlapEvent(
          title = title,
          eventYear = yr,
          description = desc,
          profileAgeAtEvent = ageAtEvent
        )
      } else null
    }
  }

  fun getPhilosophicalQuotes(): List<PhilosophicalQuote> = listOf(
    PhilosophicalQuote(
      quote = "It is not that we have a short space of time, but that we waste much of it.",
      author = "Seneca the Younger",
      era = "De Brevitate Vitae, 49 AD"
    ),
    PhilosophicalQuote(
      quote = "Remember how fast things pass away, both in the world and in time.",
      author = "Marcus Aurelius",
      era = "Meditations, 170 AD"
    ),
    PhilosophicalQuote(
      quote = "The distinction between the past, present and future is only a stubbornly persistent illusion.",
      author = "Albert Einstein",
      era = "Relativity Correspondence, 1955"
    ),
    PhilosophicalQuote(
      quote = "The Analytical Engine weaves algebraic patterns just as the Jacquard loom weaves flowers and leaves.",
      author = "Ada Lovelace",
      era = "Notes on the Engine, 1843"
    ),
    PhilosophicalQuote(
      quote = "Every one of us is, in the cosmic perspective, precious.",
      author = "Carl Sagan",
      era = "Cosmos, 1980"
    )
  )

  fun exportProfilesAsJson(): String {
    val sb = StringBuilder()
    sb.append("{\n  \"generator\": \"Chronos Temporal Engine\",\n  \"exportedAt\": \"${LocalDate.now()}\",\n  \"profiles\": [\n")
    _profiles.value.forEachIndexed { index, p ->
      val chrono = calculateChronometry(p.birthDate, p.birthTime)
      sb.append("    {\n")
      sb.append("      \"id\": \"${p.id}\",\n")
      sb.append("      \"name\": \"${p.name}\",\n")
      sb.append("      \"birthDate\": \"${p.birthDate}\",\n")
      sb.append("      \"birthTime\": \"${p.birthTime ?: "00:00"}\",\n")
      sb.append("      \"relationship\": \"${p.relationship.label}\",\n")
      sb.append("      \"exactAge\": \"${chrono.years}y ${chrono.months}m ${chrono.days}d\",\n")
      sb.append("      \"totalDaysLived\": ${chrono.totalDays},\n")
      sb.append("      \"totalHoursLived\": ${chrono.totalHours},\n")
      sb.append("      \"tenThousandthDay\": \"${chrono.tenThousandthDayDate}\",\n")
      sb.append("      \"billionthSecond\": \"${chrono.billionthSecondDate.toLocalDate()}\"\n")
      sb.append("    }${if (index < _profiles.value.size - 1) "," else ""}\n")
    }
    sb.append("  ]\n}")
    return sb.toString()
  }

  fun exportProfilesAsCsv(): String {
    val sb = StringBuilder()
    sb.append("ID,Name,Relationship,BirthDate,BirthTime,AgeYears,AgeMonths,AgeDays,TotalDays,TotalHours,10kDayDate,BillionthSecondDate\n")
    _profiles.value.forEach { p ->
      val c = calculateChronometry(p.birthDate, p.birthTime)
      sb.append("${p.id},\"${p.name}\",${p.relationship.label},${p.birthDate},${p.birthTime ?: "00:00"},${c.years},${c.months},${c.days},${c.totalDays},${c.totalHours},${c.tenThousandthDayDate},${c.billionthSecondDate.toLocalDate()}\n")
    }
    return sb.toString()
  }
}
