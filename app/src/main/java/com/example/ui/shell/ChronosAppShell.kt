package com.example.ui.shell

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ChronosTab
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.biodiff.BioDiffScreen
import com.example.ui.overview.OverviewScreen
import com.example.ui.planetary.PlanetaryScreen
import com.example.ui.profiles.ProfileFormBottomSheet
import com.example.ui.profiles.ProfileRegistryScreen
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronosAppShell(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
  val listState = rememberLazyListState()

  val isFabExpanded by remember {
    derivedStateOf { listState.firstVisibleItemIndex == 0 }
  }

  BackHandler(enabled = uiState.selectedTab != ChronosTab.OVERVIEW) {
    onIntent(ChronosUiIntent.SelectTab(ChronosTab.OVERVIEW))
  }

  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let {
      snackbarHostState.showSnackbar(it)
      onIntent(ChronosUiIntent.DismissToast)
    }
  }

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isExpandedScreen = maxWidth >= 600.dp

    Row(modifier = Modifier.fillMaxSize()) {
      if (isExpandedScreen) {
        NavigationRail(
          modifier = Modifier.fillMaxHeight(),
          containerColor = MaterialTheme.colorScheme.surface
        ) {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "CHRONOS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
          )

          ChronosTab.entries.forEach { tab ->
            NavigationRailItem(
              selected = uiState.selectedTab == tab,
              onClick = { onIntent(ChronosUiIntent.SelectTab(tab)) },
              icon = {
                Icon(
                  imageVector = getTabIcon(tab),
                  contentDescription = tab.title
                )
              },
              label = {
                Text(
                  text = tab.title,
                  maxLines = 1,
                  overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
              },
              modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}")
            )
          }
        }
      }

      Scaffold(
        modifier = Modifier
          .weight(1f)
          .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
          LargeTopAppBar(
            title = {
              Column {
                Text(
                  text = when (uiState.selectedTab) {
                    ChronosTab.OVERVIEW -> "Chronos Age Engine"
                    ChronosTab.PROFILES -> "Family & Profile Registry"
                    ChronosTab.PLANETARY -> "Planetary Chronometry"
                    ChronosTab.BIO_DIFF -> "Bio-Metrics & Time Diff"
                    ChronosTab.ANALYTICS -> "Life Grid & Analytics"
                  },
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                uiState.activeProfile?.let { active ->
                  Text(
                    text = "${active.name} • Born ${active.birthDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                  )
                }
              }
            },
            actions = {
              IconButton(
                onClick = {
                  val active = uiState.activeProfile
                  val chrono = uiState.chronometry
                  if (active != null) {
                    val summary = "${active.name} is exactly ${chrono.years} years, ${chrono.months} months, and ${chrono.days} days old today (${chrono.totalDays} total days on Earth) via Chronos."
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Chronos Age", summary))
                    Toast.makeText(context, "Copied age summary to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                },
                modifier = Modifier.testTag("action_share_summary")
              ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share Summary")
              }
            },
            scrollBehavior = scrollBehavior
          )
        },
        bottomBar = {
          if (!isExpandedScreen) {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface
            ) {
              ChronosTab.entries.forEach { tab ->
                NavigationBarItem(
                  selected = uiState.selectedTab == tab,
                  onClick = { onIntent(ChronosUiIntent.SelectTab(tab)) },
                  icon = {
                    if (tab == ChronosTab.PROFILES && uiState.profiles.size > 1) {
                      BadgedBox(
                        badge = {
                          Badge { Text("${uiState.profiles.size}", maxLines = 1) }
                        }
                      ) {
                        Icon(getTabIcon(tab), contentDescription = tab.title)
                      }
                    } else {
                      Icon(getTabIcon(tab), contentDescription = tab.title)
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      maxLines = 1,
                      overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                  },
                  modifier = Modifier.testTag("nav_bar_${tab.name.lowercase()}")
                )
              }
            }
          }
        },
        floatingActionButton = {
          if (uiState.selectedTab == ChronosTab.PROFILES || uiState.selectedTab == ChronosTab.OVERVIEW) {
            ExtendedFloatingActionButton(
              text = {
                Text(
                  text = "New Profile",
                  maxLines = 1,
                  overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
              },
              icon = { Icon(Icons.Default.Add, contentDescription = "Add Profile") },
              onClick = { onIntent(ChronosUiIntent.OpenCreateProfileSheet) },
              expanded = isFabExpanded,
              modifier = Modifier.testTag("fab_add_profile")
            )
          }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          when (uiState.selectedTab) {
            ChronosTab.OVERVIEW -> OverviewScreen(
              uiState = uiState,
              onIntent = onIntent,
              listState = listState
            )
            ChronosTab.PROFILES -> ProfileRegistryScreen(
              uiState = uiState,
              onIntent = onIntent,
              listState = listState
            )
            ChronosTab.PLANETARY -> PlanetaryScreen(
              uiState = uiState,
              onIntent = onIntent,
              listState = listState
            )
            ChronosTab.BIO_DIFF -> BioDiffScreen(
              uiState = uiState,
              onIntent = onIntent,
              listState = listState
            )
            ChronosTab.ANALYTICS -> AnalyticsScreen(
              uiState = uiState,
              onIntent = onIntent,
              listState = listState
            )
          }
        }
      }
    }
  }

  if (uiState.isProfileSheetOpen) {
    ProfileFormBottomSheet(
      editingProfile = uiState.profileBeingEdited,
      onDismiss = { onIntent(ChronosUiIntent.CloseProfileSheet) },
      onSave = { name, dob, time, rel, colorHex, isPrimary ->
        onIntent(
          ChronosUiIntent.SaveProfile(
            id = uiState.profileBeingEdited?.id,
            name = name,
            birthDate = dob,
            birthTime = time,
            relationship = rel,
            colorHex = colorHex,
            isPrimary = isPrimary
          )
        )
      }
    )
  }
}

private fun getTabIcon(tab: ChronosTab): ImageVector = when (tab) {
  ChronosTab.OVERVIEW -> Icons.Default.HourglassTop
  ChronosTab.PROFILES -> Icons.Default.Person
  ChronosTab.PLANETARY -> Icons.Default.Public
  ChronosTab.BIO_DIFF -> Icons.AutoMirrored.Filled.CompareArrows
  ChronosTab.ANALYTICS -> Icons.Default.Timeline
}
