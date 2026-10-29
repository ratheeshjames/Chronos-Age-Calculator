package com.example.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.viewmodel.ChronosUiIntent
import com.example.viewmodel.ChronosUiState

private enum class AnalyticsSubTab(val label: String) {
  ALL("All Suite"),
  LIFE_GRID("Life Grid"),
  SHARE_SETTINGS("Share & Prefs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
  uiState: ChronosUiState,
  onIntent: (ChronosUiIntent) -> Unit,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  var selectedSubTab by rememberSaveable { mutableIntStateOf(0) }
  val tabs = AnalyticsSubTab.entries

  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      SingleChoiceSegmentedButtonRow(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("analytics_segmented_row")
      ) {
        tabs.forEachIndexed { index, tab ->
          SegmentedButton(
            selected = selectedSubTab == index,
            onClick = { selectedSubTab = index },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size)
          ) {
            Text(
              text = tab.label,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    val activeMode = tabs[selectedSubTab]

    if (activeMode == AnalyticsSubTab.ALL || activeMode == AnalyticsSubTab.LIFE_GRID) {
      item {
        LifeGridAndBatterySection(
          uiState = uiState,
          onIntent = onIntent
        )
      }
    }

    if (activeMode == AnalyticsSubTab.ALL || activeMode == AnalyticsSubTab.SHARE_SETTINGS) {
      item {
        ShareAndSettingsSection(
          uiState = uiState,
          onIntent = onIntent
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
