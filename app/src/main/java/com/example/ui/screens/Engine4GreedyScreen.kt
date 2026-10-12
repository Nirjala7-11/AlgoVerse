package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GreedyActivity(
  val id: Int,
  val name: String,
  val start: Int,
  val finish: Int
)

data class GreedyKnapsackItem(
  val name: String,
  val weight: Int,
  val value: Int,
  val ratio: Float = value.toFloat() / weight
)

@Composable
fun Engine4GreedyScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Activity Selection, 1 = Greedy vs DP Counterexample
  val scrollState = rememberScrollState()

  // Tab 0: Activity Selection
  val activities = listOf(
    GreedyActivity(1, "Act A", 1, 4),
    GreedyActivity(2, "Act B", 3, 5),
    GreedyActivity(3, "Act C", 0, 6),
    GreedyActivity(4, "Act D", 5, 7),
    GreedyActivity(5, "Act E", 3, 9),
    GreedyActivity(6, "Act F", 5, 9),
    GreedyActivity(7, "Act G", 6, 10),
    GreedyActivity(8, "Act H", 8, 11)
  )

  var activityStep by remember { mutableIntStateOf(activities.size) }

  // Tab 1: Classic Greedy vs DP Counterexample
  // Knapsack Capacity = 50
  // Item 1: w=10, v=60 (ratio 6.0)
  // Item 2: w=20, v=100 (ratio 5.0)
  // Item 3: w=30, v=120 (ratio 4.0)
  val counterexampleItems = listOf(
    GreedyKnapsackItem("Item 1", 10, 60),
    GreedyKnapsackItem("Item 2", 20, 100),
    GreedyKnapsackItem("Item 3", 30, 120)
  )
  val knapsackW = 50

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(14.dp)
  ) {
    // Header
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackToDashboard) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard", tint = MaterialTheme.colorScheme.primary)
          }
          Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Topic 4: Greedy Decisions & Counterexamples", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Locally Optimal Choices • Greedy vs DP Proofs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Button(onClick = onOpenSidebar, modifier = Modifier.testTag("e4_sidebar_button")) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Mode Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        label = { Text("Activity Selection") },
        modifier = Modifier.weight(1f)
      )
      FilterChip(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        label = { Text("Greedy vs DP Counterexample") },
        modifier = Modifier.weight(1.2f),
        leadingIcon = { Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(16.dp)) }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (selectedTab == 0) {
      // Activity Selection Visualizer
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Sorting Criterion: Sorted by Earliest Finish Time (f_i)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "A greedy algorithm chooses the compatible activity that finishes earliest, maximizing time left for remaining activities.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(12.dp))

          var lastFinishTime = 0
          var acceptedCount = 0

          activities.take(activityStep).forEachIndexed { index, act ->
            val isCompatible = act.start >= lastFinishTime
            if (isCompatible) {
              lastFinishTime = act.finish
              acceptedCount++
            }

            val itemBg = if (isCompatible) Color(0xFF065F46) else Color(0xFF7F1D1D)
            val badgeColor = if (isCompatible) Color(0xFF34D399) else Color(0xFFF87171)

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = itemBg.copy(alpha = 0.6f),
              border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (isCompatible) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "${act.name}: start=${act.start}, finish=${act.finish}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      fontFamily = FontFamily.Monospace
                    )
                  )
                }

                Text(
                  text = if (isCompatible) "ACCEPTED" else "REJECTED (overlaps)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Total Compatible Activities Chosen: $acceptedCount / ${activities.size}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
          )
        }
      }
    } else {
      // CRITICAL FEATURE: Greedy vs DP Knapsack Counterexample
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Live Counterexample: Why Greedy FAILS for 0/1 Knapsack",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Input: Knapsack Capacity W = 50 kg. Three items sorted by value/weight ratio:",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(8.dp))

          counterexampleItems.forEach { item ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E293B),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("${item.name}: w = ${item.weight} kg, v = $${item.value}", color = Color.White, style = MaterialTheme.typography.bodySmall)
                Text("Ratio: ${item.ratio} $/kg", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Side-by-side comparison
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Greedy Strategy
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF7F1D1D).copy(alpha = 0.35f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("Greedy (by Ratio)", fontWeight = FontWeight.Bold, color = Color(0xFFF87171), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("1. Takes Item 1 (w=10, v=$60, ratio 6.0). Remaining W = 40.", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Text("2. Takes Item 2 (w=20, v=$100, ratio 5.0). Remaining W = 20.", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Text("3. Item 3 (w=30) CANNOT FIT in remaining 20!", color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Total Weight: 30 / 50 kg", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
                Text("Total Value: $160", fontWeight = FontWeight.Bold, color = Color(0xFFF87171), style = MaterialTheme.typography.titleSmall)
                Text("SUBOPTIMAL ❌ (20 kg empty space wasted!)", color = Color(0xFFFCA5A5), style = MaterialTheme.typography.labelSmall)
              }
            }

            // DP Strategy (Engine 3)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF065F46).copy(alpha = 0.35f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("DP 0/1 Knapsack", fontWeight = FontWeight.Bold, color = Color(0xFF34D399), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("1. Evaluates all subproblems.", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Text("2. Selects Item 2 (w=20, v=$100).", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Text("3. Selects Item 3 (w=30, v=$120).", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Total Weight: 50 / 50 kg (Full)", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelSmall)
                Text("Total Value: $220", fontWeight = FontWeight.Bold, color = Color(0xFF34D399), style = MaterialTheme.typography.titleSmall)
                Text("OPTIMAL ✅ (Extra +$60 value!)", color = Color(0xFF6EE7B7), style = MaterialTheme.typography.labelSmall)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Teaching Takeaway: Greedy fails in 0/1 Knapsack because items cannot be broken. High-ratio items may leave empty space that cannot be filled by remaining valuable items. Greedy ONLY works for FRACTIONAL knapsack!",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFFF1F5F9)
              )
            }
          }
        }
      }
    }
  }
}
