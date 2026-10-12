package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AlgorithmType
import com.example.model.ComplexityRegion
import com.example.model.SortStep
import com.example.model.StepActionType

@Composable
fun LiveCountersCard(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  currentRegion: ComplexityRegion,
  complexityExplanation: String,
  arraySize: Int,
  modifier: Modifier = Modifier
) {
  val comparisons = currentStep?.comparisonsCount ?: 0
  val swaps = currentStep?.swapsCount ?: 0
  val remainingInversions = currentStep?.remainingInversions ?: 0
  val maxComparisons = if (arraySize > 1) arraySize * (arraySize - 1) / 2 else 1
  val comparisonProgress = (comparisons.toFloat() / maxComparisons).coerceIn(0f, 1f)

  val regionColor by animateColorAsState(
    targetValue = Color(currentRegion.badgeColorHex),
    label = "regionColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("live_counters_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Complexity Region Badge & Current Phase
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Complexity Region Live Indicator
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = regionColor.copy(alpha = 0.16f),
          border = androidx.compose.foundation.BorderStroke(1.dp, regionColor.copy(alpha = 0.6f)),
          modifier = Modifier.testTag("complexity_region_badge")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(regionColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${currentRegion.label} • ${currentRegion.bigO}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = regionColor
            )
          }
        }

        // Action Status Badge
        val actionText = when (currentStep?.actionType) {
          StepActionType.COMPARING -> "Comparing"
          StepActionType.SWAPPING -> "Swapping"
          StepActionType.MIN_FOUND -> "New Min"
          StepActionType.ELEMENT_SORTED -> "Element Finalized"
          StepActionType.EARLY_EXIT -> "Early Exit"
          StepActionType.COMPLETE -> "Complete"
          else -> "Ready"
        }
        val actionColor = when (currentStep?.actionType) {
          StepActionType.COMPARING -> Color(0xFFF59E0B)
          StepActionType.SWAPPING -> Color(0xFFEF4444)
          StepActionType.ELEMENT_SORTED, StepActionType.COMPLETE, StepActionType.EARLY_EXIT -> Color(0xFF10B981)
          StepActionType.MIN_FOUND -> Color(0xFF8B5CF6)
          else -> MaterialTheme.colorScheme.primary
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = actionColor.copy(alpha = 0.15f)
        ) {
          Text(
            text = actionText,
            color = actionColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3 Counter Metrics Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricTile(
          icon = Icons.Default.CompareArrows,
          label = "Comparisons",
          value = "$comparisons",
          subtext = "Max: $maxComparisons",
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )

        MetricTile(
          icon = Icons.Default.SwapHoriz,
          label = "Swaps Made",
          value = "$swaps",
          subtext = if (algorithm == AlgorithmType.BUBBLE_SORT) "Bubble swaps" else "Min placements",
          color = Color(0xFFEF4444),
          modifier = Modifier.weight(1f)
        )

        MetricTile(
          icon = Icons.Default.Loop,
          label = "Inversions",
          value = "$remainingInversions",
          subtext = if (remainingInversions == 0) "Sorted!" else "Remaining",
          color = if (remainingInversions == 0) Color(0xFF10B981) else Color(0xFFF59E0B),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Comparison Progress Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Comparison Density: ${(comparisonProgress * 100).toInt()}%",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "$comparisons / $maxComparisons",
          style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { comparisonProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = regionColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      // Complexity Explanation Callout
      if (complexityExplanation.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = regionColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = complexityExplanation,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
private fun MetricTile(
  icon: ImageVector,
  label: String,
  value: String,
  subtext: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtext,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )
    }
  }
}
