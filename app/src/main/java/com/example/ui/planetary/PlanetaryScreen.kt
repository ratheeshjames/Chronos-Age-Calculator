package com.example.ui.planetary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LunarPhaseInfo
import com.example.data.Planet
import com.example.data.PlanetaryAge
import com.example.ui.theme.BottomSheetExpressiveShape
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChipExpressiveShape
import com.example.ui.theme.ChronosMotion
import com.example.ui.theme.SolarGold
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private enum class PlanetarySortOrder(val label: String) {
  DISTANCE("Solar Order"),
  NEXT_BIRTHDAY("Next Birthday"),
  OLDEST_AGE("Highest Age")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetaryScreen(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  var viewModeIndex by rememberSaveable { mutableIntStateOf(0) } // 0 = Orbital Cards, 1 = Matrix Table
  var sortOrderName by rememberSaveable { mutableStateOf(PlanetarySortOrder.DISTANCE.name) }
  var highlightedPlanetName by rememberSaveable { mutableStateOf(Planet.MARS.name) }
  var selectedSheetPlanetName by rememberSaveable { mutableStateOf<String?>(null) }

  val sortOrder = remember(sortOrderName) {
    PlanetarySortOrder.entries.find { it.name == sortOrderName } ?: PlanetarySortOrder.DISTANCE
  }
  val highlightedPlanet = remember(highlightedPlanetName) {
    Planet.entries.find { it.name == highlightedPlanetName } ?: Planet.MARS
  }
  val selectedSheetPlanetAge = remember(uiState.planetaryAges, selectedSheetPlanetName) {
    uiState.planetaryAges.find { it.planet.name == selectedSheetPlanetName }
  }

  val sortedPlanets = remember(uiState.planetaryAges, sortOrder) {
    when (sortOrder) {
      PlanetarySortOrder.DISTANCE -> uiState.planetaryAges.sortedBy { it.planet.distanceAu }
      PlanetarySortOrder.NEXT_BIRTHDAY -> uiState.planetaryAges.sortedBy { it.nextBirthdayDays }
      PlanetarySortOrder.OLDEST_AGE -> uiState.planetaryAges.sortedByDescending { it.ageInPlanetaryYears }
    }
  }

  val lunarInfo = uiState.chronometry.lunarPhaseAtBirth

  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Interactive Solar System Orbit Visualizer (F-048)
    item(key = "solar_visualizer") {
      Spacer(modifier = Modifier.height(4.dp))
      SolarSystemOrbitVisualizerCard(
        planetaryAges = uiState.planetaryAges,
        highlightedPlanet = highlightedPlanet,
        onSelectPlanet = { planet ->
          highlightedPlanetName = planet.name
        },
        onInspectPlanet = { planetAge ->
          selectedSheetPlanetName = planetAge.planet.name
        }
      )
    }

    // 2. Lunar Age & Moon Phase at Birth Card (F-049)
    item(key = "lunar_card") {
      LunarChronometryCard(lunarInfo = lunarInfo)
    }

    // 3. View Mode Segmented Control & Sort Filter Chips (F-050)
    item(key = "planetary_controls") {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Interplanetary Chronometry",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = "Tap any planet for telemetry",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1
          )
        }

        SingleChoiceSegmentedButtonRow(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("planetary_view_segmented")
        ) {
          val modes = listOf("Orbital Cards", "Comparison Matrix")
          modes.forEachIndexed { index, label ->
            SegmentedButton(
              selected = viewModeIndex == index,
              onClick = { viewModeIndex = index },
              shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
            ) {
              Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          PlanetarySortOrder.entries.forEach { order ->
            FilterChip(
              selected = sortOrder == order,
              onClick = { sortOrderName = order.name },
              label = {
                Text(
                  text = order.label,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              },
              shape = ChipExpressiveShape
            )
          }
        }
      }
    }

    // 4. Planetary Display (Cards vs Compact Matrix) (F-039 to F-047)
    if (viewModeIndex == 0) {
      items(
        items = sortedPlanets,
        key = { it.planet.name }
      ) { planetAge ->
        PlanetaryOrbitalCard(
          planetAge = planetAge,
          isHighlighted = planetAge.planet == highlightedPlanet,
          onClick = {
            highlightedPlanetName = planetAge.planet.name
            selectedSheetPlanetName = planetAge.planet.name
          }
        )
      }
    } else {
      item(key = "planetary_matrix_table") {
        InterplanetaryMatrixTable(
          planetaryAges = sortedPlanets,
          onSelectPlanet = { planetAge ->
            highlightedPlanetName = planetAge.planet.name
            selectedSheetPlanetName = planetAge.planet.name
          }
        )
      }
    }

    item(key = "bottom_spacer") {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Planetary Telemetry ModalBottomSheet
  selectedSheetPlanetAge?.let { planetAge ->
    PlanetaryTelemetryBottomSheet(
      planetAge = planetAge,
      onDismiss = { selectedSheetPlanetName = null }
    )
  }
}

@Composable
private fun SolarSystemOrbitVisualizerCard(
  planetaryAges: List<PlanetaryAge>,
  highlightedPlanet: Planet,
  onSelectPlanet: (Planet) -> Unit,
  onInspectPlanet: (PlanetaryAge) -> Unit
) {
  val highlightedAge = planetaryAges.find { it.planet == highlightedPlanet } ?: planetaryAges.firstOrNull()

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_solar_visualizer"),
    shape = CardExpressiveShape,
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF0E1324)
    )
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "KEPLERIAN ORBITAL HELIO-SYNC",
            style = MaterialTheme.typography.labelSmall,
            color = SolarGold,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.4.sp
          )
          Text(
            text = "Live Solar System Positions",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
        Icon(
          imageVector = Icons.Default.Public,
          contentDescription = "Solar Orbit",
          tint = SolarGold
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Concentric Orbits Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(210.dp)) {
          val center = Offset(size.width / 2f, size.height / 2f)
          val maxRadius = size.minDimension / 2f - 8.dp.toPx()

          // Draw glowing Sun in center
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFFFEA00), Color(0xFFFF8800), Color.Transparent),
              center = center,
              radius = 22.dp.toPx()
            ),
            radius = 22.dp.toPx(),
            center = center
          )
          drawCircle(
            color = Color(0xFFFFD60A),
            radius = 9.dp.toPx(),
            center = center
          )

          // Draw 8 concentric planetary orbits
          val visiblePlanets = planetaryAges.take(8)
          visiblePlanets.forEachIndexed { idx, pAge ->
            val ringRadius = 22.dp.toPx() + ((idx + 1).toFloat() / visiblePlanets.size.toFloat()) * (maxRadius - 22.dp.toPx())
            val isTarget = pAge.planet == highlightedPlanet
            val pColor = Color(pAge.planet.colorHex)

            drawCircle(
              color = if (isTarget) pColor.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.14f),
              radius = ringRadius,
              center = center,
              style = Stroke(width = if (isTarget) 2.2.dp.toPx() else 1.dp.toPx())
            )

            // Angle based on orbitProgressFraction (0f..1f -> -PI/2 .. 3PI/2)
            val angleRad = (pAge.orbitProgressFraction * 2.0 * PI) - (PI / 2.0)
            val px = center.x + (ringRadius * cos(angleRad)).toFloat()
            val py = center.y + (ringRadius * sin(angleRad)).toFloat()

            if (isTarget) {
              drawCircle(
                color = pColor.copy(alpha = 0.35f),
                radius = 9.dp.toPx(),
                center = Offset(px, py)
              )
            }
            drawCircle(
              color = pColor,
              radius = if (isTarget) 5.5.dp.toPx() else 3.5.dp.toPx(),
              center = Offset(px, py)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Planet Selector Pills
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        planetaryAges.forEach { pAge ->
          val selected = pAge.planet == highlightedPlanet
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(
                if (selected) Color(pAge.planet.colorHex)
                else Color.White.copy(alpha = 0.1f)
              )
              .clickable { onSelectPlanet(pAge.planet) }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = pAge.planet.displayName,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      if (highlightedAge != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onInspectPlanet(highlightedAge) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${highlightedAge.planet.displayName}: ${String.format("%.2f", highlightedAge.ageInPlanetaryYears)} Solar Years",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "Orbit ${(highlightedAge.orbitProgressFraction * 100).toInt()}% complete • Next birthday in ${highlightedAge.nextBirthdayDays} Earth days",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.75f),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(SolarGold.copy(alpha = 0.18f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "Inspect →",
              style = MaterialTheme.typography.labelMedium,
              color = SolarGold,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LunarChronometryCard(lunarInfo: LunarPhaseInfo) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_lunar_phase"),
    shape = CardExpressiveShape,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = "Lunar Sync",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "LUNAR SYNODIC PHASE AT BIRTH",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "${lunarInfo.emoji} ${lunarInfo.phaseName} (${lunarInfo.illuminationPercent}% Lit)",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = String.format(
            "%,.1f Lunar Months lived • Next Full Moon in %d days",
            lunarInfo.lunarCyclesLived,
            lunarInfo.nextFullMoonDays
          ),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun PlanetaryOrbitalCard(
  planetAge: PlanetaryAge,
  isHighlighted: Boolean,
  onClick: () -> Unit
) {
  val animatedSweep by animateFloatAsState(
    targetValue = planetAge.orbitProgressFraction * 360f,
    animationSpec = ChronosMotion.ExpressiveSpringFloat,
    label = "orbit_sweep_${planetAge.planet.name}"
  )
  val planetColor = Color(planetAge.planet.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("planet_card_${planetAge.planet.name.lowercase()}"),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(
      containerColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 3.dp else 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Custom Canvas Orbital Progress Ring
        Box(
          modifier = Modifier.size(56.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(52.dp)) {
            drawArc(
              color = planetColor.copy(alpha = 0.2f),
              startAngle = 0f,
              sweepAngle = 360f,
              useCenter = false,
              style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
              color = planetColor,
              startAngle = -90f,
              sweepAngle = animatedSweep,
              useCenter = false,
              style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
          }
          Text(
            text = "${(planetAge.orbitProgressFraction * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }

        Column {
          Text(
            text = planetAge.planet.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = planetAge.planet.tagline,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Next orbit: ${planetAge.nextBirthdayDate} (${planetAge.nextBirthdayDays}d)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = String.format("%.2f", planetAge.ageInPlanetaryYears),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Black,
          color = planetColor
        )
        Text(
          text = "SOLAR YRS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
  }
}

@Composable
private fun InterplanetaryMatrixTable(
  planetaryAges: List<PlanetaryAge>,
  onSelectPlanet: (PlanetaryAge) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("card_planetary_matrix"),
    shape = CardExpressiveShape
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Planet", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
        Text("Solar Age", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Orbit Pd", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Next B-Day", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
      }
      HorizontalDivider()

      planetaryAges.forEach { pAge ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectPlanet(pAge) }
            .padding(vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1.2f)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(pAge.planet.colorHex))
            )
            Text(pAge.planet.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
          }
          Text(
            text = String.format("%.2f y", pAge.ageInPlanetaryYears),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = "${pAge.planet.orbitalDays.toInt()}d",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
          )
          Text(
            text = "in ${pAge.nextBirthdayDays}d",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
          )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanetaryTelemetryBottomSheet(
  planetAge: PlanetaryAge,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  var earthWeightKg by rememberSaveable { mutableFloatStateOf(70f) }
  val planet = planetAge.planet
  val planetColor = Color(planet.colorHex)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = BottomSheetExpressiveShape
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(planetColor),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Public, contentDescription = planet.displayName, tint = Color.White)
          }
          Column {
            Text(
              text = "${planet.displayName} Chronometry",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = planet.tagline,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      // Main Planetary Stats Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Solar Age", style = MaterialTheme.typography.labelMedium)
            Text(
              text = String.format("%.3f yrs", planetAge.ageInPlanetaryYears),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "${planetAge.completedOrbits} full orbits completed",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Planetary Days", style = MaterialTheme.typography.labelMedium)
            Text(
              text = String.format("%,.1f sols", planetAge.planetaryDaysLived),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
              text = "Day length: ${planet.rotationHours}h",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }

      // Upcoming Planetary Birthdays
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.Cake, contentDescription = "Next Birthday", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(
              text = "Next ${planet.displayName} Birthday (Orbit #${planetAge.completedOrbits + 1})",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }
          Text(
            text = "${planetAge.nextBirthdayDate} • In ${planetAge.nextBirthdayDays} Earth days",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
          LinearProgressIndicator(
            progress = { planetAge.orbitProgressFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(CircleShape),
            color = planetColor
          )
        }
      }

      // Surface Gravity Weight Converter
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Surface Gravity Telemetry (${planet.gravityFactor}g)",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = String.format("%.1f kg on %s", earthWeightKg * planet.gravityFactor, planet.displayName),
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Black
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Reference Earth Mass: ${earthWeightKg.toInt()} kg • Distance: ${planet.distanceAu} AU from Sun",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Slider(
            value = earthWeightKg,
            onValueChange = { earthWeightKg = it },
            valueRange = 30f..140f
          )
        }
      }
    }
  }
}
