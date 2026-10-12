package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AlgorithmComplexityInfo(
  val id: String,
  val name: String,
  val paradigm: String,
  val category: String,
  val topicId: String,
  val bestTime: String,
  val avgTime: String,
  val worstTime: String,
  val spaceComplexity: String,
  val stability: String,
  val keyCondition: String
)

object ComplexityCatalog {
  val allAlgorithms = listOf(
    AlgorithmComplexityInfo(
      id = "bubble_sort",
      name = "Bubble Sort",
      paradigm = "In-Place Exchanging",
      category = "Sorting",
      topicId = "bubble_sort",
      bestTime = "Ω(n)",
      avgTime = "Θ(n²)",
      worstTime = "O(n²)",
      spaceComplexity = "O(1) Aux",
      stability = "Stable ✓",
      keyCondition = "Ω(n) requires swapped boolean flag on sorted input"
    ),
    AlgorithmComplexityInfo(
      id = "selection_sort",
      name = "Selection Sort",
      paradigm = "Selection by Extrema",
      category = "Sorting",
      topicId = "selection_sort",
      bestTime = "Ω(n²)",
      avgTime = "Θ(n²)",
      worstTime = "O(n²)",
      spaceComplexity = "O(1) Aux",
      stability = "Unstable ✗",
      keyCondition = "Minimizes memory writes (at most n-1 swaps total)"
    ),
    AlgorithmComplexityInfo(
      id = "insertion_sort",
      name = "Insertion Sort",
      paradigm = "Incremental Insertion",
      category = "Sorting",
      topicId = "bubble_sort",
      bestTime = "Ω(n)",
      avgTime = "Θ(n²)",
      worstTime = "O(n²)",
      spaceComplexity = "O(1) Aux",
      stability = "Stable ✓",
      keyCondition = "Optimal for small n or nearly-sorted datasets"
    ),
    AlgorithmComplexityInfo(
      id = "merge_sort",
      name = "Merge Sort",
      paradigm = "Divide & Conquer",
      category = "Sorting",
      topicId = "merge_sort_tree",
      bestTime = "Ω(n log n)",
      avgTime = "Θ(n log n)",
      worstTime = "O(n log n)",
      spaceComplexity = "O(n) Aux",
      stability = "Stable ✓",
      keyCondition = "T(n) = 2T(n/2) + O(n); Master Theorem Case 2"
    ),
    AlgorithmComplexityInfo(
      id = "knapsack_01",
      name = "0/1 Knapsack (DP)",
      paradigm = "Dynamic Programming",
      category = "DP & Greedy",
      topicId = "knapsack_01_dp",
      bestTime = "Ω(n·W)",
      avgTime = "Θ(n·W)",
      worstTime = "O(n·W)",
      spaceComplexity = "O(n·W) or O(W)",
      stability = "Optimal Substr.",
      keyCondition = "Pseudo-polynomial time dependent on capacity W"
    ),
    AlgorithmComplexityInfo(
      id = "activity_selection",
      name = "Activity Selection",
      paradigm = "Greedy Strategy",
      category = "DP & Greedy",
      topicId = "greedy_vs_dp_knapsack",
      bestTime = "Ω(n log n)",
      avgTime = "Θ(n log n)",
      worstTime = "O(n log n)",
      spaceComplexity = "O(1) Aux",
      stability = "Greedy Choice",
      keyCondition = "Sort activities by finish time; optimal substructure"
    ),
    AlgorithmComplexityInfo(
      id = "dijkstra",
      name = "Dijkstra's Algorithm",
      paradigm = "Greedy Relaxation",
      category = "Graphs & Search",
      topicId = "dijkstra_graph",
      bestTime = "Ω((V+E) log V)",
      avgTime = "Θ((V+E) log V)",
      worstTime = "O((V+E) log V)",
      spaceComplexity = "O(V) PQ & Dist",
      stability = "Non-neg Edges",
      keyCondition = "Min-heap Priority Queue; fails on negative cycles"
    ),
    AlgorithmComplexityInfo(
      id = "n_queens",
      name = "N-Queens",
      paradigm = "Backtracking & Pruning",
      category = "Graphs & Search",
      topicId = "n_queens_pruning",
      bestTime = "Ω(n)",
      avgTime = "Pruned Sub-exp",
      worstTime = "O(n!)",
      spaceComplexity = "O(n) Stack",
      stability = "State Pruning",
      keyCondition = "Bounding functions check column and 45° diagonal safety"
    )
  )
}

/**
 * Decluttered, horizontal algorithm complexity summary deck.
 * Displays Best, Average, and Worst-case time and space complexity in a readable table format.
 * Uses a horizontal card carousel to preserve vertical space and keep the dashboard clean.
 */
@Composable
fun AlgorithmComplexitySummaryDeck(
  onNavigateTopic: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("All") }
  val filters = listOf("All", "Sorting", "DP & Greedy", "Graphs & Search")

  val filteredList = when (selectedFilter) {
    "All" -> ComplexityCatalog.allAlgorithms
    else -> ComplexityCatalog.allAlgorithms.filter { it.category == selectedFilter }
  }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("algorithm_complexity_summary_deck")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header with Title & Filter
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.TableChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Algorithm Complexity Matrix",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Best, Average, Worst & Space bounds summary",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Direct Head-to-Head Comparative Race Button
        FilledTonalButton(
          onClick = { onNavigateTopic("compare_algorithms") },
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .height(32.dp)
            .testTag("launch_compare_race_from_deck_button")
        ) {
          Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Race 2 Algos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Compact category filter chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        filters.forEach { filter ->
          val isSelected = selectedFilter == filter
          FilterChip(
            selected = isSelected,
            onClick = { selectedFilter = filter },
            label = {
              Text(
                text = filter,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
              selectedBorderColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.height(30.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Horizontal Swipeable Deck of Complexity Cards (Eliminates vertical clutter!)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        filteredList.forEach { algo ->
          AlgorithmComplexityCard(
            item = algo,
            onPractice = { onNavigateTopic(algo.topicId) }
          )
        }
      }
    }
  }
}

/**
 * Individual algorithm summary card with readable complexity table format.
 */
@Composable
private fun AlgorithmComplexityCard(
  item: AlgorithmComplexityInfo,
  onPractice: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    ),
    modifier = Modifier
      .width(285.dp)
      .testTag("complexity_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Algorithm Name & Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
          Text(
            text = item.paradigm,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Readable Table Format for Complexities
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          // Table Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "METRIC",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
            Text(
              text = "BOUND / VALUE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

          // Row 1: Best Case Time
          ComplexityTableRow(
            metric = "Best Time",
            value = item.bestTime,
            valueColor = Color(0xFF10B981) // Green
          )

          // Row 2: Average Case Time
          ComplexityTableRow(
            metric = "Average Time",
            value = item.avgTime,
            valueColor = Color(0xFF0284C7) // Sky
          )

          // Row 3: Worst Case Time
          ComplexityTableRow(
            metric = "Worst Time",
            value = item.worstTime,
            valueColor = if (item.worstTime.contains("n²") || item.worstTime.contains("n!")) Color(0xFFEF4444) else Color(0xFFD97706)
          )

          // Row 4: Space Complexity
          ComplexityTableRow(
            metric = "Space (Aux)",
            value = item.spaceComplexity,
            valueColor = MaterialTheme.colorScheme.primary
          )

          // Row 5: Stability / Characteristic
          ComplexityTableRow(
            metric = "Stability",
            value = item.stability,
            valueColor = if (item.stability.contains("✓")) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Key condition footer
      Text(
        text = "💡 ${item.keyCondition}",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Practice Button
      Button(
        onClick = onPractice,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(34.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Study Algorithm",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}

@Composable
private fun ComplexityTableRow(
  metric: String,
  value: String,
  valueColor: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = metric,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Surface(
      shape = RoundedCornerShape(4.dp),
      color = valueColor.copy(alpha = 0.12f)
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          color = valueColor
        ),
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
      )
    }
  }
}
