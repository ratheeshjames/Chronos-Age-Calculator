package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardExpressiveShape
import com.example.ui.theme.ChronosMotion
import kotlinx.coroutines.launch

data class OnboardingStep(
  val title: String,
  val subtitle: String,
  val badge: String,
  val icon: ImageVector,
  val accentColor: Color
)

@Composable
fun OnboardingScreen(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val steps = listOf(
    OnboardingStep(
      title = "Exact Down to the Second",
      subtitle = "Calculate your exact age in years, months, days, hours, and seconds with leap-year precision.",
      badge = "CHRONOMETRY",
      icon = Icons.Default.HourglassTop,
      accentColor = MaterialTheme.colorScheme.primary
    ),
    OnboardingStep(
      title = "Across the Solar System",
      subtitle = "Track your planetary age on Mars, Venus, Jupiter, and beyond with astronomical orbital accuracy.",
      badge = "ASTRONOMY",
      icon = Icons.Default.Public,
      accentColor = Color(0xFFE76F51)
    ),
    OnboardingStep(
      title = "Milestones & Life Metrics",
      subtitle = "Count down to your next 10,000-day mark, 1 billion seconds lived, and approximate lifetime heartbeats.",
      badge = "INTELLIGENCE",
      icon = Icons.Default.Timeline,
      accentColor = Color(0xFF2A9D8F)
    )
  )

  val pagerState = rememberPagerState(pageCount = { steps.size })
  val coroutineScope = rememberCoroutineScope()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .padding(horizontal = 24.dp, vertical = 20.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Brand Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CHRONOS",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          letterSpacing = 2.sp,
          color = MaterialTheme.colorScheme.primary
        )

        if (pagerState.currentPage < steps.size - 1) {
          OutlinedButton(
            onClick = onComplete,
            modifier = Modifier.testTag("skip_onboarding_button")
          ) {
            Text("Skip")
          }
        }
      }

      // Middle Carousel
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) { page ->
        val step = steps[page]
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .height(320.dp),
            shape = CardExpressiveShape,
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.radialGradient(
                    colors = listOf(
                      step.accentColor.copy(alpha = 0.25f),
                      Color.Transparent
                    )
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(step.accentColor.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = step.icon,
                    contentDescription = step.title,
                    tint = step.accentColor,
                    modifier = Modifier.size(48.dp)
                  )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                  text = step.badge,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = step.accentColor,
                  letterSpacing = 1.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(28.dp))

          Text(
            text = step.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = step.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }
      }

      // Bottom Navigation and Controls
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Dot Indicators
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 24.dp)
        ) {
          repeat(steps.size) { index ->
            val isSelected = pagerState.currentPage == index
            val width by animateDpAsState(
              targetValue = if (isSelected) 28.dp else 8.dp,
              animationSpec = ChronosMotion.ExpressiveSpringDp,
              label = "dot_width"
            )
            Box(
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(8.dp)
                .width(width)
                .clip(CircleShape)
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.outlineVariant
                )
            )
          }
        }

        // Action Button
        AnimatedVisibility(
          visible = pagerState.currentPage == steps.size - 1,
          enter = fadeIn() + scaleIn(animationSpec = ChronosMotion.ExpressiveSpringFloat),
          exit = fadeOut() + scaleOut()
        ) {
          Button(
            onClick = onComplete,
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("get_started_button"),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Text(
              text = "Launch Chronos",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Get Started"
            )
          }
        }

        AnimatedVisibility(
          visible = pagerState.currentPage < steps.size - 1,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Button(
            onClick = {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("next_onboarding_button"),
            shape = MaterialTheme.shapes.large
          ) {
            Text("Next")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next"
            )
          }
        }
      }
    }
  }
}
