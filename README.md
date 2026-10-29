# Chronos: Precision Age Calculator & Temporal Intelligence Engine

![Android API 24+](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3 Expressive](https://img.shields.io/badge/Material%203-Expressive%20Design-7C4DFF?style=for-the-badge&logo=materialdesign&logoColor=white)
![License MIT](https://img.shields.io/badge/License-MIT-FFB300?style=for-the-badge)
![Build Passing](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-00C853?style=for-the-badge&logo=githubactions&logoColor=white)

**Chronos** is a flagship Android temporal intelligence engine and precision age calculator built 100% with **Kotlin** and **Jetpack Compose**. Designed around **Material 3 Expressive (M3E)** principles, Chronos delivers real-time down-to-the-millisecond chronometry, multi-profile registry management, heliocentric solar system age calculations, biological rhythm telemetry, comparative profile time-diff analytics, and a 100-year life grid visualizer.

---

## Visual & Design Philosophy (Material 3 Expressive)

- **Expressive Shape Hierarchy**: Sculpted container geometry utilizing `28.dp` expressive card corners (`CardExpressiveShape`), `32.dp` modal bottom sheets (`ModalExpressiveShape`), and `16.dp` interactive filter/status chips (`ChipExpressiveShape`).
- **Physics-Driven Motion**: Custom spring animations (`dampingRatio = 0.72f`, `stiffness = Spring.StiffnessMediumLow`) power tabular digit tickers, live heartbeat icon scales, orbital progress canvases, and celebratory confetti bursts.
- **Tonal & Dynamic Color Palettes**: Four curated high-contrast themes (**Cosmic Indigo**, **Solar Gold**, **Emerald Chrono**, **Monochrome Slate**) paired with Android 12+ Dynamic Wallpaper colors (`dynamicDarkColorScheme` / `dynamicLightColorScheme`) and a pure `#000000` **OLED Pitch-Black** mode.
- **Tabular Monospace Readouts**: Live ticking counters use `fontFeatureSettings = "tnum"` to eliminate horizontal layout jitter during 60fps millisecond and 1Hz second updates.

---

## Architectural Overview

Chronos strictly enforces **Clean Architecture** and **Unidirectional Data Flow (UDF)**:

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                         UI Layer (Jetpack Compose)                      │
│  ChronosAppShell • OverviewScreen • ProfileRegistryScreen               │
│  PlanetaryScreen • BioDiffScreen  • AnalyticsScreen                     │
└───────────────────┬─────────────────────────────────────▲───────────────┘
                    │ ChronosUiIntent                     │ StateFlow<ChronosUiState>
                    ▼                                     │
┌─────────────────────────────────────────────────────────────────────────┐
│                    Presentation Layer (ChronosViewModel)                │
│  • Single Source of Truth (MutableStateFlow<ChronosUiState>)            │
│  • Adaptive Coroutine Ticker (1000ms 1Hz / 50ms High-Frequency Millis)  │
│  • Intent Reducer & Reactive Derived Computations                       │
└───────────────────┬─────────────────────────────────────▲───────────────┘
                    │ Pure Domain Calls                   │ Immutable Domain Models
                    ▼                                     │
┌─────────────────────────────────────────────────────────────────────────┐
│                 Domain & Data Layer (ChronosRepository)                 │
│  • Precision Calendar Period & Intercalary Leap-Year Engine             │
│  • NASA Heliocentric Planetary Orbital & Lunar Synodical Algorithms     │
│  • Biometric Vital Telemetry, Comparative Diff & Date Arithmetic Engine │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Complete 100-Feature Specification Matrix

### Module 1: Core Precision Chronometry (`F-001` – `F-015`)
- [x] **F-001**: Real-time canonical age breakdown in Years, Months, and Days (`java.time.Period`).
- [x] **F-002**: Live ticking Hours, Minutes, and Seconds counter with `1Hz` coroutine synchronization.
- [x] **F-003**: High-frequency Millisecond sub-ticker (`50ms` interval toggle) with tabular monospace digits.
- [x] **F-004**: Interactive Material 3 `DatePickerDialog` for instant birth date selection.
- [x] **F-005**: Interactive Material 3 `TimePickerDialog` for exact hour-and-minute birth time precision.
- [x] **F-006**: Total elapsed Days lived counter with one-tap clipboard copy.
- [x] **F-007**: Total elapsed Weeks lived counter with remainder days breakdown.
- [x] **F-008**: Total elapsed Hours lived counter with comma-formatted readouts.
- [x] **F-009**: Total elapsed Minutes lived counter.
- [x] **F-010**: Total elapsed Seconds lived live ticker.
- [x] **F-011**: Leap-year intercalary compensator counting exact Feb 29 leap days experienced (`leapDaysLived`).
- [x] **F-012**: Exact list of every leap year lived through since birth (`leapYearsList`).
- [x] **F-013**: Birth day-of-the-week badge with historical weekday insight.
- [x] **F-014**: Current year progress ring & decimal age readout (e.g., `31.842910 yrs`).
- [x] **F-015**: Birth vs. current astronomical Julian Day Number (`JDN`) and Unix Epoch timestamp inspector.

### Module 2: Multi-Profile & Family Registry (`F-016` – `F-027`)
- [x] **F-016**: Pre-seeded interactive profiles (Alex Rivera, Elena Vance, Leo Rivera, Albert Einstein, Ada Lovelace).
- [x] **F-017**: Horizontal quick-switcher profile bar pinned to the top app shell.
- [x] **F-018**: Full CRUD Profile Creation & Editing via `ProfileFormBottomSheet`.
- [x] **F-019**: Profile deletion with confirmation and automatic active-profile fallback.
- [x] **F-020**: Relationship category tagging (`Self`, `Partner`, `Child`, `Parent`, `Friend`, `Historical`).
- [x] **F-021**: Custom avatar badge monogram and 8-color Material Expressive swatch selector.
- [x] **F-022**: Favorite/Pinned profile toggle (`isFavorite`) for priority ordering.
- [x] **F-023**: Real-time search bar filtering profiles by name, notes, or relationship tag.
- [x] **F-024**: Category filter chips with live profile counts.
- [x] **F-025**: Multi-criteria sorting (`Name A–Z`, `Youngest First`, `Oldest First`, `Soonest Birthday`).
- [x] **F-026**: Profile notes and birth city/timezone metadata support.
- [x] **F-027**: Instant "Set Active" profile switching across all 5 analytical modules.

### Module 3: Temporal Milestones & Countdown Engine (`F-028` – `F-038`)
- [x] **F-028**: Next Earth birthday live countdown (Months, Days, Hours, Minutes, Seconds remaining).
- [x] **F-029**: Next birthday weekday prediction and upcoming age badge.
- [x] **F-030**: Half-birthday (+6 months) date and countdown calculator.
- [x] **F-031**: `10,000 Days on Earth` milestone tracker with completion percentage and target date.
- [x] **F-032**: `25,000 Days on Earth` longevity milestone tracker.
- [x] **F-033**: `1,000,000,000 Seconds` (1 Gigasecond) exact date and progress tracker.
- [x] **F-034**: `500,000 Hours` & `1,000,000 Hours` chronological milestone badges.
- [x] **F-035**: `1,000 Weeks` and `4,000 Weeks` life-stage milestone cards.
- [x] **F-036**: Milestone status filter (`Completed` vs. `Upcoming` milestones).
- [x] **F-037**: Custom user-authored event/milestone countdown creator (`CustomMilestone`).
- [x] **F-038**: Custom milestone deletion and "Time Since / Time Until" automatic mode detection.

### Module 4: Planetary & Astronomical Chronometry (`F-039` – `F-050`)
- [x] **F-039**: Interactive custom `Canvas` Solar System heliocentric orbit visualizer with live orbital angles.
- [x] **F-040**: Mercury chronometry (`87.9691` Earth-day orbit) — solar years, sols, and next Mercury birthday.
- [x] **F-041**: Venus chronometry (`224.701` Earth-day orbit) — solar years, sols, and next Venus birthday.
- [x] **F-042**: Earth reference orbital telemetry (`365.25636` sidereal days).
- [x] **F-043**: Mars chronometry (`686.98` Earth-day orbit) — Martian years and Martian sols lived.
- [x] **F-044**: Jupiter chronometry (`11.862615` Earth-year orbit) — Jovian years and fast-rotation days.
- [x] **F-045**: Saturn chronometry (`29.447498` Earth-year orbit) — Saturnian ring-cycle years.
- [x] **F-046**: Uranus & Neptune deep-space chronometry (`84.0168` and `164.7913` Earth-year orbits).
- [x] **F-047**: Interactive Planetary Inspector modal with surface gravity weight converter.
- [x] **F-048**: Lunar phase at birth calculator (`29.53059`-day synodic cycle) with custom moon illumination `Canvas`.
- [x] **F-049**: Total full moons (`lunationsExperienced`) witnessed since birth.
- [x] **F-050**: Western Zodiac, Chinese Sexagenary Zodiac (Element + Animal), Birthstone, Birth Flower, and Generation Cohort (`Gen Z`, `Millennial`, `Gen X`, etc.) cards.

### Module 5: Biological & Life Rhythm Telemetry (`F-051` – `F-058`)
- [x] **F-051**: Real-time estimated cumulative heartbeats lived (`estimatedHeartbeats`) with pulsing heart animation.
- [x] **F-052**: Cumulative blood volume pumped in liters (`bloodPumpedLiters`).
- [x] **F-053**: Estimated cumulative breaths taken (`estimatedBreaths`) and air volume inhaled (`airInhaledCubicMeters`).
- [x] **F-054**: Estimated cumulative eye blinks (`estimatedBlinks`).
- [x] **F-055**: Cumulative sleep duration tracker in total hours and equivalent years spent asleep.
- [x] **F-056**: REM dream-cycle estimator (`dreamHoursEstimated`).
- [x] **F-057**: Hair growth (`meters`) and fingernail growth (`meters`) cumulative biological estimators.
- [x] **F-058**: Interactive Resting Heart Rate (`45–110 BPM`) and Daily Sleep (`4–12 hrs/day`) calibration sliders.

### Module 6: Comparative Time Diff & Date Arithmetic (`F-059` – `F-070`)
- [x] **F-059**: Dual-profile selector comparing any two profiles side-by-side.
- [x] **F-060**: One-tap profile swap button with animated comparison transition.
- [x] **F-061**: Exact chronological gap in Years, Months, Days, Total Days, and Total Hours.
- [x] **F-062**: Live dynamic Age Ratio calculator (e.g., `Person A is 1.420x older than Person B`).
- [x] **F-063**: Double-Age Convergence Date calculator (exact date when the older person is `2.0x` the younger person's age).
- [x] **F-064**: Shared life-overlap percentage bar.
- [x] **F-065**: Multi-profile Group/Family Cohort statistics (combined collective years, mean age, median age, age span).
- [x] **F-066**: Date Arithmetic Calculator (`Add` or `Subtract` arbitrary Years, Months, Weeks, and Days from any base date).
- [x] **F-067**: ISO-8601 Week Date formatter (`YYYY-Www-D`) and Quarter-of-Year (`Q1–Q4`) identifier for target dates.
- [x] **F-068**: Working Days (Mon–Fri) vs. Weekend Days (Sat–Sun) lived breakdown with stacked visual bar.
- [x] **F-069**: Retirement target age countdown (`targetRetirementAge` configurable slider).
- [x] **F-070**: Historical "Epoch Twin" age comparison against famous scientific pioneers.

### Module 7: Visual Analytics & 100-Year Life Grid (`F-071` – `F-085`)
- [x] **F-071**: "Life Battery" longevity gauge with animated charge bar and customizable baseline expectancy (`60–110 yrs`).
- [x] **F-072**: Remaining days, hours, and active waking hours estimator.
- [x] **F-073**: Interactive **100-Year Life Grid** (`10x10` matrix of years: Lived, Current Pulsing Year, Projected Target, Bonus Century).
- [x] **F-074**: Tap-to-inspect Life Grid cell modal showing exact calendar year, decade stage, and cumulative days at that age.
- [x] **F-075**: **5,200-Week Memento Mori Canvas** visualizing lived vs. remaining weeks of an 80–100 year human life.
- [x] **F-076**: Four-Season Counter tracking exact Springs, Summers, Autumns, and Winters experienced.
- [x] **F-077**: Life-Stage Distribution Bar breaking total life hours into Sleeping, Working/Study, Eating, and Free/Creative time.
- [x] **F-078**: Unlockable Chronos Achievement Badges (`1,000 Days`, `5,000 Days`, `10,000 Days`, `100,000 Hours`, `500,000 Hours`, `1B Seconds`).
- [x] **F-079**: Decade-by-decade life phase timeline indicator.
- [x] **F-080**: Celebratory particle confetti overlay triggered on milestone inspection or achievement unlock.

### Module 8: Export, Greeting Cards & System Personalization (`F-081` – `F-100`)
- [x] **F-081**: Live customizable **Milestone Greeting Card Studio** with 4 gradient themes (`Cosmic Aurora`, `Solar Flare`, `Emerald Nebula`, `Royal Obsidian`).
- [x] **F-082**: Curated philosophical & scientific temporal quote stamps for greeting cards.
- [x] **F-083**: One-tap formatted **Share Age Summary** via Android native `Intent.ACTION_SEND` share sheet.
- [x] **F-084**: One-tap clipboard export of formatted profile chronometry report.
- [x] **F-085**: Full multi-profile **JSON Data Export** generator with preview & copy/share actions.
- [x] **F-086**: Full multi-profile **CSV Spreadsheet Export** generator for external analytics.
- [x] **F-087**: Theme Mode selector (`System Default`, `Light Mode`, `Dark Mode`).
- [x] **F-088**: **OLED Pitch-Black** (`#000000`) power-saving surface toggle for AMOLED displays.
- [x] **F-089**: **Android 12+ Dynamic Color** (`Material You`) live toggle.
- [x] **F-090**: 4 Expressive Accent Palettes (`Cosmic Indigo`, `Solar Gold`, `Emerald Chrono`, `Monochrome Slate`).
- [x] **F-091**: High-Frequency Millisecond Ticker toggle (`50ms` vs. `1000ms` battery-saver mode).
- [x] **F-092**: 24-Hour vs. 12-Hour time format preference toggle.
- [x] **F-093**: Haptic feedback toggle for interactive sliders, pickers, and buttons.
- [x] **F-094**: Adaptive Navigation Suite (`NavigationBar` on compact phones, `NavigationRail` on expanded tablets/foldables).
- [x] **F-095**: Collapsible `LargeTopAppBar` with `exitUntilCollapsedScrollBehavior` and live ticker pill.
- [x] **F-096**: Edge-to-edge system bar integration (`enableEdgeToEdge()`).
- [x] **F-097**: Full TalkBack accessibility semantics and `testTag` instrumentation across all interactive nodes.
- [x] **F-098**: Overflow-safe responsive typography preventing single-character vertical wrapping on compact screens.
- [x] **F-099**: Predictive back handler (`BackHandler`) returning smoothly from secondary tabs and modals to Live Age.
- [x] **F-100**: One-tap **Reset Demo State** restoring factory seed profiles and default biometric calibration.

---

## Local Build & Run Instructions

### Prerequisites
- **Android Studio**: Koala / Ladybug (2024.1+) or newer.
- **JDK**: Java 17 (Temurin recommended).
- **Android SDK**: `compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`.

### Building from the Command Line (CLI)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<your-org>/chronos-age-calculator.git
   cd chronos-age-calculator
   ```

2. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The compiled debug APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Build the Release APK:**
   ```bash
   ./gradlew assembleRelease
   ```
   The signed release APK will be generated at:
   `app/build/outputs/apk/release/app-release.apk`

4. **Run Unit & Robolectric Tests:**
   ```bash
   ./gradlew :app:testDebugUnitTest
   ```

### Building & Releasing via GitHub Actions
- **Continuous Integration**: Every push to `main` triggers `.github/workflows/build_and_release.yml`, compiling the APK and uploading it as a workflow artifact.
- **Automated GitHub Releases**: Pushing a semantic version tag (e.g., `git tag v1.0.0 && git push origin v1.0.0`) or triggering the workflow manually via `workflow_dispatch` compiles the standalone APK, attaches `RELEASE_NOTES.md`, and publishes an official GitHub Release.
