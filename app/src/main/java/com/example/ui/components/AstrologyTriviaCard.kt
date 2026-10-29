package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ChronometricResult
import com.example.ui.theme.CardExpressiveShape

@Composable
fun AstrologyTriviaCard(
  chrono: ChronometricResult,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_astrology_trivia"),
    shape = CardExpressiveShape,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Text(
        text = "Astrological & Cultural Chronometry",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SuggestionChip(
          onClick = {},
          label = { Text("Zodiac: ${chrono.westernZodiac}") },
          modifier = Modifier.weight(1f)
        )
        SuggestionChip(
          onClick = {},
          label = { Text("Lunar: ${chrono.lunarZodiac}") },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SuggestionChip(
          onClick = {},
          label = { Text("Gem: ${chrono.birthstone}") },
          modifier = Modifier.weight(1f)
        )
        SuggestionChip(
          onClick = {},
          label = { Text("Flower: ${chrono.birthFlower.take(14)}") },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Trivia",
          tint = MaterialTheme.colorScheme.primary
        )
        Text(
          text = "\"${chrono.dayOfWeekTrivia}\" (${chrono.dayOfWeekBorn})",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
