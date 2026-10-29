# Chronos: Precision Age Calculator & Temporal Intelligence Engine — `v1.0.0`

- **Release Tag:** `v1.0.0`
- **Version Name:** `1.0` (`versionCode = 1`)
- **Minimum SDK:** Android 7.0+ / 8.0+ (`minSdk = 24`, adaptive icons `API 26+`)
- **Target & Compile SDK:** Android 16 (`targetSdk = 36`, `compileSdk = 36`)
- **UI Framework:** 100% Jetpack Compose + Material 3 Expressive (`M3E`)

---

## Core Highlights

- **Material 3 Expressive UI System:** Built with sculpted `28.dp` container cards, `32.dp` modal sheets, physics-based spring animations (`dampingRatio = 0.72f`), and tabular monospace (`tnum`) real-time counters.
- **Dynamic & OLED Theming:** Supports Android 12+ Material You Dynamic Colors, 4 custom expressive color palettes (**Cosmic Indigo**, **Solar Gold**, **Emerald Chrono**, **Monochrome Slate**), and a true `#000000` **OLED Pitch-Black** mode.
- **Zero-Stub Interactive State Engine:** Powered by an unthrottled coroutine ticker (`50ms` millisecond precision or `1Hz` battery-friendly mode) with 5 pre-seeded historical and family profiles ready for immediate exploration.

---

## 100-Feature Breakdown Across 8 Modules

1. **Module 1 — Core Precision Chronometry (F-001 to F-015):**
   - Exact age in Years, Months, Days, Hours, Minutes, Seconds, and live Milliseconds.
   - Total Days, Weeks, Hours, Minutes, and Seconds lived with one-tap clipboard copy.
   - Leap-year intercalary day counter (`Feb 29` occurrences + exact leap year list), Julian Day Numbers (`JDN`), and Unix Epoch inspector.
2. **Module 2 — Multi-Profile & Family Registry (F-016 to F-027):**
   - Full CRUD profile management with relationship tags (`Self`, `Partner`, `Child`, `Parent`, `Friend`, `Historical`), custom monograms, color swatches, search, category filters, and multi-mode sorting.
3. **Module 3 — Temporal Milestones & Countdown Engine (F-028 to F-038):**
   - Live countdowns to next birthday, half-birthday, `10,000 Days`, `25,000 Days`, `1,000,000,000 Seconds` (1 Gigasecond), `500,000 Hours`, and custom user-authored milestones.
4. **Module 4 — Planetary & Astronomical Chronometry (F-039 to F-050):**
   - Interactive heliocentric Solar System orbital canvas, exact age and next birthday across Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, and Neptune, planetary gravity weight converter, birth Lunar Phase canvas, and Western/Chinese zodiac insights.
5. **Module 5 — Biological & Life Rhythm Telemetry (F-051 to F-058):**
   - Real-time estimated heartbeats, blood volume pumped, breaths taken, air volume inhaled, eye blinks, sleep years accumulated, dream hours, and interactive BPM/sleep calibration sliders.
6. **Module 6 — Comparative Time Diff & Date Arithmetic (F-059 to F-070):**
   - Dual-profile side-by-side age diff, live age ratio (`1.42x`), double-age convergence date calculator, family cohort statistics, Add/Subtract Date Arithmetic calculator, ISO-8601 week dates, and Weekday vs. Weekend lived breakdown.
7. **Module 7 — Visual Analytics & 100-Year Life Grid (F-071 to F-080):**
   - Customizable Life Battery gauge, interactive `10x10` 100-Year Life Grid, 5,200-Week Memento Mori dot matrix, 4-Season counter, life-stage hour allocation bar, and unlockable milestone badges.
8. **Module 8 — Export, Greeting Cards & Personalization (F-081 to F-100):**
   - Shareable Milestone Greeting Card Studio, native Android share sheet integration, structured JSON and CSV profile exporters, adaptive navigation (`NavigationBar` / `NavigationRail`), and full TalkBack/testTag support.

---

## Known Behavior & System Requirements

- **System Compatibility:** Compatible with devices running Android 7.0 (API 24) and Android 8.0+ (API 26+) through Android 16 (API 36).
- **Dynamic Color Support:** Material You dynamic wallpaper extraction requires Android 12+ (API 31+); devices on API 24–30 automatically use the selected Chronos Expressive palette.
- **Offline-First Operation:** All astronomical, chronometric, and biological calculations run 100% on-device with zero network latency or external API dependencies.

---

## Binary Verification & Checksums

| Artifact Name | Variant | MD5 Checksum | SHA-256 Checksum |
| :--- | :--- | :--- | :--- |
| `Chronos-Age-Calculator-v1.0.0-release.apk` | `release` | `<MD5_CHECKSUM_GENERATED_BY_CI>` | `<SHA256_CHECKSUM_GENERATED_BY_CI>` |
| `Chronos-Age-Calculator-v1.0.0-debug.apk` | `debug` | `<MD5_CHECKSUM_GENERATED_BY_CI>` | `<SHA256_CHECKSUM_GENERATED_BY_CI>` |

To verify your downloaded APK binary locally:

```bash
sha256sum Chronos-Age-Calculator-v1.0.0-release.apk
md5sum Chronos-Age-Calculator-v1.0.0-release.apk
```
