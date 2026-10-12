package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.SortStep

@Composable
fun DualCompareCard(
  bubbleSteps: List<SortStep>,
  selectionSteps: List<SortStep>,
  arraySize: Int,
  modifier: Modifier = Modifier
) {
  val bubbleFinal = bubbleSteps.lastOrNull()
  val selectionFinal = selectionSteps.lastOrNull()

  val bubbleComparisons = bubbleFinal?.comparisonsCount ?: 0
  val bubbleSwaps = bubbleFinal?.swapsCount ?: 0

  val selectionComparisons = selectionFinal?.comparisonsCount ?: 0
  val selectionSwaps = selectionFinal?.swapsCount ?: 0

  val bubbleWinsComparisons = bubbleComparisons < selectionComparisons
  val selectionWinsSwaps = selectionSwaps <= bubbleSwaps

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("dual_compare_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Compare,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Side-by-Side Algorithm Comparison",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Benchmarking both algorithms on your custom input array of $arraySize elements:",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Dual Column Comparison Table
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Bubble Sort Column
        AlgoBenchColumn(
          title = "Bubble Sort",
          accentColor = Color(0xFF3B82F6),
          comparisons = bubbleComparisons,
          swaps = bubbleSwaps,
          isAdaptive = true,
          isStable = true,
          modifier = Modifier.weight(1f)
        )

        // Selection Sort Column
        AlgoBenchColumn(
          title = "Selection Sort",
          accentColor = Color(0xFF8B5CF6),
          comparisons = selectionComparisons,
          swaps = selectionSwaps,
          isAdaptive = false,
          isStable = false,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(12.dp))

      // Pedagogical Takeaway
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
          .padding(10.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .size(20.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "ADA Key Intuition:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (bubbleComparisons < selectionComparisons) {
              "Bubble Sort won on comparisons ($bubbleComparisons vs $selectionComparisons) because its early-exit flag detected partially sorted subarrays, whereas Selection Sort is non-adaptive and always performs n(n-1)/2 comparisons."
            } else if (selectionSwaps < bubbleSwaps) {
              "Selection Sort won on memory writes ($selectionSwaps swaps vs $bubbleSwaps swaps) because it only swaps once per outer loop pass (at most n-1 swaps), while Bubble Sort swaps adjacent inverted pairs repeatedly."
            } else {
              "Selection Sort guarantees at most n-1 swaps regardless of input permutation. Bubble Sort can execute in O(n) when already sorted, but can require up to n(n-1)/2 swaps in reverse order."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun AlgoBenchColumn(
  title: String,
  accentColor: Color,
  comparisons: Int,
  swaps: Int,
  isAdaptive: Boolean,
  isStable: Boolean,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = accentColor
      )

      Spacer(modifier = Modifier.height(8.dp))

      StatRow(label = "Comparisons", value = "$comparisons")
      Spacer(modifier = Modifier.height(4.dp))
      StatRow(label = "Swaps", value = "$swaps")
      Spacer(modifier = Modifier.height(4.dp))
      StatRow(label = "Total Ops", value = "${comparisons + swaps}")

      Spacer(modifier = Modifier.height(8.dp))

      FeatureTag(
        label = "Adaptive:",
        enabled = isAdaptive,
        caption = if (isAdaptive) "Early O(n) exit" else "Always O(n²)"
      )
      Spacer(modifier = Modifier.height(4.dp))
      FeatureTag(
        label = "Stable:",
        enabled = isStable,
        caption = if (isStable) "Preserves order" else "May alter order"
      )
    }
  }
}

@Composable
private fun StatRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun FeatureTag(label: String, enabled: Boolean, caption: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = if (enabled) Icons.Default.Check else Icons.Default.Close,
      contentDescription = null,
      tint = if (enabled) Color(0xFF10B981) else Color(0xFFEF4444),
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = "$label $caption",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
