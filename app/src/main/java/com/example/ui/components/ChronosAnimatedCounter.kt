package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NumericOdometerStyle

@Composable
fun RollingNumberText(
  value: String,
  style: TextStyle = NumericOdometerStyle,
  color: Color = MaterialTheme.colorScheme.primary,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    value.forEach { char ->
      AnimatedContent(
        targetState = char,
        transitionSpec = {
          if (targetState > initialState) {
            slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
          } else {
            slideInVertically { height -> -height } togetherWith slideOutVertically { height -> height }
          }
        },
        label = "char_odometer"
      ) { targetChar ->
        Text(
          text = targetChar.toString(),
          style = style,
          color = color,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun InteractiveChronosUnitCell(
  value: Int,
  label: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  Column(
    modifier = modifier
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .testTag("unit_cell_${label.lowercase()}"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    AnimatedContent(
      targetState = value,
      transitionSpec = {
        slideInVertically { height -> height / 2 } togetherWith slideOutVertically { height -> -height / 2 }
      },
      label = "unit_number_$label"
    ) { targetValue ->
      Text(
        text = targetValue.toString(),
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Black,
        color = color
      )
    }

    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.2.sp,
      color = color.copy(alpha = 0.85f)
    )
  }
}
