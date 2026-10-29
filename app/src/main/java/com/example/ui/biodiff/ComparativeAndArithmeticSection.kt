package com.example.ui.biodiff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BottomSheetExpressiveShape
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.ui.theme.EmeraldMilestone
import com.example.ui.theme.SolarGold
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun ComparativeProfileSection(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  modifier: Modifier = Modifier
) {
  val comp = uiState.comparativeResult
  val cohort = uiState.groupCohortStats

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Dual-Profile Selector & Swap Controls (F-059)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_dual_profile_selector"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "DUAL-ENTITY DIFFERENTIAL MATRIX",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 1.2.sp
            )
            Text(
              text = "Compare Any Two Timelines",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          FilledTonalIconButton(
            onClick = { onIntent(ChronosUiIntent.SwapComparisonProfiles) },
            modifier = Modifier.testTag("btn_swap_comparison")
          ) {
            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Profiles")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Primary Profile (Entity A)", style = MaterialTheme.typography.labelSmall)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          uiState.profiles.forEach { p ->
            FilterChip(
              selected = p.id == uiState.activeProfile?.id,
              onClick = { onIntent(ChronosUiIntent.SelectActiveProfile(p.id)) },
              label = { Text(p.name) },
              shape = ChipExpressiveShape
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Comparison Profile (Entity B)", style = MaterialTheme.typography.labelSmall)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          uiState.profiles.filter { it.id != uiState.activeProfile?.id }.forEach { p ->
            FilterChip(
              selected = p.id == uiState.secondaryProfileId,
              onClick = { onIntent(ChronosUiIntent.SelectSecondaryProfile(p.id)) },
              label = { Text(p.name) },
              shape = ChipExpressiveShape
            )
          }
        }
      }
    }

    // 2. Exact Age Difference & Ratio Results (F-060 – F-064)
    if (comp != null) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_comparison_delta"),
        shape = CardExpressiveShape,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        )
      ) {
        Column(modifier = Modifier.padding(22.dp)) {
          Text(
            text = "${comp.olderProfileName} is Senior to ${comp.youngerProfileName}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "${comp.yearsDifference}y • ${comp.monthsDifference}m • ${comp.daysDifference}d",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Exact Gap: %,d days (%,d hours)".format(comp.totalDaysDifference, comp.totalHoursDifference),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Ratio Multiplier (F-062)
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(12.dp)
            ) {
              Column {
                Text("Age Ratio Multiplier", style = MaterialTheme.typography.labelSmall)
                Text(
                  text = String.format("%.3fx", comp.ageRatio),
                  style = MaterialTheme.typography.titleLarge,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            // Combined Lifespan Sum (F-064)
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(12.dp)
            ) {
              Column {
                Text("Combined Lifespan", style = MaterialTheme.typography.labelSmall)
                Text(
                  text = "${comp.combinedTotalYears} yrs",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Black,
                  color = MaterialTheme.colorScheme.secondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Double-Age & 1.5x Convergence Dates (F-063)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.medium)
              .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Milestone Convergence Points",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "• 2.0x Double-Age Date: ${comp.doubleAgeConvergenceDate} (${comp.olderProfileName} is exactly twice ${comp.youngerProfileName}'s age)",
                style = MaterialTheme.typography.bodySmall
              )
              Text(
                text = "• 1.5x Sesqui-Age Date: ${comp.sesquiAgeConvergenceDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 3. Multi-Profile Cohort Cluster Analyzer (F-065)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_cohort_analyzer"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Groups, contentDescription = "Cohort", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "Group Cohort Average & Median Age",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Select profiles to include in family/team cluster",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          uiState.profiles.forEach { profile ->
            val inCohort = profile.id in uiState.selectedCohortProfileIds
            FilterChip(
              selected = inCohort,
              onClick = { onIntent(ChronosUiIntent.ToggleCohortSelection(profile.id)) },
              label = { Text(profile.name) },
              shape = ChipExpressiveShape
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Mean Age", style = MaterialTheme.typography.labelSmall)
            Text(
              text = String.format("%.1f yrs", cohort.averageAgeYears),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Column {
            Text("Median Age", style = MaterialTheme.typography.labelSmall)
            Text(
              text = String.format("%.1f yrs", cohort.medianAgeYears),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary
            )
          }
          Column {
            Text("Gen Span", style = MaterialTheme.typography.labelSmall)
            Text(
              text = "${cohort.generationSpanYears} yrs",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.tertiary
            )
          }
        }
      }
    }

    // 4. Historical Timeline Event Overlap (F-070)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_historical_overlap"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.HistoryEdu, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "Historical Timeline Overlap",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "World milestones during ${uiState.activeProfile?.name ?: "your"}'s lifespan",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (uiState.historicalEvents.isEmpty()) {
          Text(
            text = "Born after recent cataloged historical anchors — future history in the making!",
            style = MaterialTheme.typography.bodySmall
          )
        } else {
          uiState.historicalEvents.forEach { ev ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${ev.eventYear} • ${ev.title}",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = ev.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer)
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Age ${ev.profileAgeAtEvent}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateArithmeticAndMilestonesSection(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  modifier: Modifier = Modifier
) {
  val arith = uiState.dateArithmetic
  val workday = uiState.chronometry.workdayBreakdown

  var showBaseDatePicker by rememberSaveable { mutableStateOf(false) }
  var showAddMilestoneSheet by rememberSaveable { mutableStateOf(false) }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Date Offset Addition & Subtraction Calculator (F-066 & F-067)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_date_arithmetic"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Temporal Date Arithmetic Engine",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        // Base Date Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Anchor Base Date", style = MaterialTheme.typography.labelSmall)
            Text(
              text = "${arith.baseDate} (${arith.baseDate.dayOfWeek})",
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(
              onClick = {
                val dob = uiState.activeProfile?.birthDate ?: LocalDate.now()
                onIntent(
                  ChronosUiIntent.UpdateDateArithmetic(
                    dob, arith.isAddition, arith.yearsOffset, arith.monthsOffset, arith.weeksOffset, arith.daysOffset
                  )
                )
              }
            ) {
              Text("Use DOB", maxLines = 1)
            }
            OutlinedButton(onClick = { showBaseDatePicker = true }) {
              Text("Pick Date", maxLines = 1)
            }
          }
        }

        // Add vs Subtract Segmented Control
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
          SegmentedButton(
            selected = arith.isAddition,
            onClick = {
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, true, arith.yearsOffset, arith.monthsOffset, arith.weeksOffset, arith.daysOffset
                )
              )
            },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
          ) {
            Text("+ Add Span", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
          SegmentedButton(
            selected = !arith.isAddition,
            onClick = {
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, false, arith.yearsOffset, arith.monthsOffset, arith.weeksOffset, arith.daysOffset
                )
              )
            },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
          ) {
            Text("− Subtract Span", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
        }

        // Steppers for Years, Months, Weeks, Days
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ArithmeticStepperBox(
            label = "Years",
            value = arith.yearsOffset,
            onChange = { newY ->
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, arith.isAddition, newY.coerceAtLeast(0), arith.monthsOffset, arith.weeksOffset, arith.daysOffset
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
          ArithmeticStepperBox(
            label = "Months",
            value = arith.monthsOffset,
            onChange = { newM ->
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, arith.isAddition, arith.yearsOffset, newM.coerceAtLeast(0), arith.weeksOffset, arith.daysOffset
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ArithmeticStepperBox(
            label = "Weeks",
            value = arith.weeksOffset,
            onChange = { newW ->
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, arith.isAddition, arith.yearsOffset, arith.monthsOffset, newW.coerceAtLeast(0), arith.daysOffset
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
          ArithmeticStepperBox(
            label = "Days",
            value = arith.daysOffset,
            onChange = { newD ->
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  arith.baseDate, arith.isAddition, arith.yearsOffset, arith.monthsOffset, arith.weeksOffset, newD.coerceAtLeast(0)
                )
              )
            },
            modifier = Modifier.weight(1f)
          )
        }

        // Resulting Date Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "RESULTING TARGET DATE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
              )
              Text(
                text = "${arith.resultingDate}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "${arith.resultingDayOfWeek} • Net Delta: ${arith.totalDaysDelta} days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }
    }

    // 2. Working Days vs. Weekend Days Lived & Day-of-Year Progress (F-068 & F-069)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_workday_analysis"),
      shape = CardExpressiveShape,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
      )
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.WorkOutline, contentDescription = "Workdays", tint = MaterialTheme.colorScheme.primary)
          Column {
            Text(
              text = "Working Days vs. Weekend Counter",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Day ${workday.currentDayOfYear} of ${workday.totalDaysInCurrentYear} in current calendar year",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Stacked Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(CircleShape)
        ) {
          Box(
            modifier = Modifier
              .weight(workday.weekdayPercentage.coerceAtLeast(0.05f))
              .fillMaxHeight()
              .background(MaterialTheme.colorScheme.primary)
          )
          Box(
            modifier = Modifier
              .weight((1f - workday.weekdayPercentage).coerceAtLeast(0.05f))
              .fillMaxHeight()
              .background(SolarGold)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Weekdays (Mon–Fri)", style = MaterialTheme.typography.labelSmall)
            Text(
              text = "${"%,d".format(workday.weekdayDaysLived)} days (${(workday.weekdayPercentage * 100).toInt()}%)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Weekends (Sat–Sun)", style = MaterialTheme.typography.labelSmall)
            Text(
              text = "${"%,d".format(workday.weekendDaysLived)} days (${((1f - workday.weekdayPercentage) * 100).toInt()}%)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = SolarGold
            )
          }
        }
      }
    }

    // 3. Custom Event & Milestone Countdowns (F-037 & F-038)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("card_custom_milestones"),
      shape = CardExpressiveShape
    ) {
      Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Time Since / Time Until Events",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "Custom temporal milestones & celebration triggers",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { showAddMilestoneSheet = true },
            modifier = Modifier
              .defaultMinSize(minHeight = 48.dp)
              .testTag("btn_add_custom_milestone"),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
          ) {
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("New", maxLines = 1)
          }
        }

        uiState.customMilestones.forEach { ms ->
          val daysDelta = ChronoUnit.DAYS.between(LocalDate.now(), ms.targetDate)
          val isPast = daysDelta < 0
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.medium)
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(Color(ms.accentColorHex))
              )
              Column {
                Text(
                  text = ms.title,
                  style = MaterialTheme.typography.bodyLarge,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${ms.category} • ${ms.targetDate}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = if (daysDelta == 0L) "Today! 🎉" else if (isPast) "${-daysDelta}d ago" else "in ${daysDelta}d",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Black,
                  color = if (isPast) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                  maxLines = 1
                )
              }
              IconButton(onClick = { onIntent(ChronosUiIntent.TriggerCelebration) }) {
                Icon(
                  imageVector = Icons.Default.Celebration,
                  contentDescription = "Celebrate",
                  tint = EmeraldMilestone,
                  modifier = Modifier.size(20.dp)
                )
              }
              IconButton(onClick = { onIntent(ChronosUiIntent.DeleteCustomMilestone(ms.id)) }) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Remove",
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Base Date Picker Dialog
  if (showBaseDatePicker) {
    val pickerState = rememberDatePickerState(
      initialSelectedDateMillis = arith.baseDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    DatePickerDialog(
      onDismissRequest = { showBaseDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            pickerState.selectedDateMillis?.let { millis ->
              val picked = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
              onIntent(
                ChronosUiIntent.UpdateDateArithmetic(
                  picked, arith.isAddition, arith.yearsOffset, arith.monthsOffset, arith.weeksOffset, arith.daysOffset
                )
              )
            }
            showBaseDatePicker = false
          }
        ) {
          Text("Select")
        }
      },
      dismissButton = {
        TextButton(onClick = { showBaseDatePicker = false }) {
          Text("Cancel")
        }
      }
    ) {
      DatePicker(state = pickerState)
    }
  }

  // Custom Milestone Creator ModalBottomSheet
  if (showAddMilestoneSheet) {
    AddCustomMilestoneBottomSheet(
      onDismiss = { showAddMilestoneSheet = false },
      onSave = { title, date, category ->
        onIntent(ChronosUiIntent.AddCustomMilestone(title, date, category, 0xFF2A9D8F))
        showAddMilestoneSheet = false
      }
    )
  }
}

@Composable
private fun ArithmeticStepperBox(
  label: String,
  value: Int,
  onChange: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(onClick = { onChange((value - 1).coerceAtLeast(0)) }, modifier = Modifier.size(48.dp)) {
          Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
        }
        Text(
          text = "$value",
          style = MaterialTheme.typography.titleMedium,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )
        IconButton(onClick = { onChange(value + 1) }, modifier = Modifier.size(48.dp)) {
          Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomMilestoneBottomSheet(
  onDismiss: () -> Unit,
  onSave: (String, LocalDate, String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  var title by rememberSaveable { mutableStateOf("") }
  var category by rememberSaveable { mutableStateOf("Milestone") }
  var targetDateEpochDay by rememberSaveable { mutableLongStateOf(LocalDate.now().plusDays(100).toEpochDay()) }
  var showDatePicker by rememberSaveable { mutableStateOf(false) }

  val targetDate = LocalDate.ofEpochDay(targetDateEpochDay)

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
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text(
        text = "Create Custom Countdown / Event",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Event Title", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        placeholder = { Text("e.g. Sabbatical Trip, Graduation", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Target Event Date", style = MaterialTheme.typography.labelSmall)
          Text("$targetDate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
          onClick = { showDatePicker = true },
          modifier = Modifier.defaultMinSize(minHeight = 48.dp),
          contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
          Icon(Icons.Default.CalendarMonth, contentDescription = "Pick", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Change Date", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("Milestone", "Career", "Adventure", "Astronomy").forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            shape = ChipExpressiveShape
          )
        }
      }

      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(title.trim(), targetDate, category)
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .defaultMinSize(minHeight = 50.dp),
        contentPadding = ButtonDefaults.ContentPadding
      ) {
        Text("Save Custom Milestone", maxLines = 1, overflow = TextOverflow.Ellipsis)
      }
    }
  }

  if (showDatePicker) {
    val pickerState = rememberDatePickerState(
      initialSelectedDateMillis = targetDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            pickerState.selectedDateMillis?.let { millis ->
              targetDateEpochDay = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate().toEpochDay()
            }
            showDatePicker = false
          }
        ) {
          Text("Confirm")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text("Cancel")
        }
      }
    ) {
      DatePicker(state = pickerState)
    }
  }
}
