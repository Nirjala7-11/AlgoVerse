package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DpItem(val name: String, val weight: Int, val value: Int)

@Composable
fun Engine3DpScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Preset items for 0/1 Knapsack
  val items = listOf(
    DpItem("Item 1", 2, 3),
    DpItem("Item 2", 3, 4),
    DpItem("Item 3", 4, 5),
    DpItem("Item 4", 5, 8)
  )
  val maxCapacity = 7
  val n = items.size

  // Compute 2D DP Table
  // dp[i][w] = max value using first i items with capacity w
  val dpTable = remember(items, maxCapacity) {
    val table = Array(n + 1) { IntArray(maxCapacity + 1) }
    for (i in 1..n) {
      val item = items[i - 1]
      for (w in 0..maxCapacity) {
        if (item.weight <= w) {
          table[i][w] = maxOf(table[i - 1][w], table[i - 1][w - item.weight] + item.value)
        } else {
          table[i][w] = table[i - 1][w]
        }
      }
    }
    table
  }

  // Precomputed traceback path (cells that yielded optimal solution)
  val optimalPath = remember(dpTable) {
    val path = mutableSetOf<Pair<Int, Int>>()
    var currI = n
    var currW = maxCapacity
    while (currI > 0 && currW > 0) {
      path.add(Pair(currI, currW))
      val item = items[currI - 1]
      if (dpTable[currI][currW] != dpTable[currI - 1][currW]) {
        // Item was chosen!
        currW -= item.weight
      }
      currI -= 1
    }
    path.add(Pair(0, currW))
    path.toSet()
  }

  // State: step-by-step filling
  val totalCells = (n + 1) * (maxCapacity + 1)
  var fillProgressCell by remember { mutableIntStateOf(totalCells) } // Start filled or animated
  var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(Pair(n, maxCapacity)) }
  var isTracebackMode by remember { mutableStateOf(true) }

  val horizontalScroll = rememberScrollState()
  val verticalScroll = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(verticalScroll)
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
          Icon(Icons.Default.GridOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Topic 3: DP Table Grid Engine", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("0/1 Knapsack • Recurrence & Traceback", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Button(onClick = onOpenSidebar, modifier = Modifier.testTag("e3_sidebar_button")) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Recurrence Formula Callout
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("Bellman Recurrence Relation for 0/1 Knapsack", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          "dp[i][w] = max( dp[i-1][w], dp[i-1][w - w_i] + v_i )",
          style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
        )

        val cell = selectedCell
        if (cell != null && cell.first > 0) {
          val (r, c) = cell
          val item = items[r - 1]
          val withoutVal = dpTable[r - 1][c]
          val fits = item.weight <= c
          val withVal = if (fits) dpTable[r - 1][c - item.weight] + item.value else -1

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Inspecting cell ($r, $c): ${item.name} (w=${item.weight}, v=${item.value}) -> " +
                if (fits) "max(exclude: $withoutVal, include: $withVal) = ${dpTable[r][c]}" else "item exceeds capacity -> exclude: $withoutVal",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF34D399)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Traceback Mode Switch & Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilterChip(
        selected = isTracebackMode,
        onClick = { isTracebackMode = !isTracebackMode },
        label = { Text("Traceback Mode (Optimal Path)") },
        leadingIcon = { Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(16.dp)) },
        modifier = Modifier.testTag("traceback_toggle")
      )

      Row {
        Button(
          onClick = { fillProgressCell = totalCells },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
          Text("Fill Grid", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2D DP Table Grid Rendered with Horizontal Scroll
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dp_table_grid_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(horizontalScroll)
          .padding(12.dp)
      ) {
        // Table Header: Capacity Columns (0 .. W)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .width(76.dp)
              .height(38.dp)
              .background(Color(0xFF1E293B), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text("i \\ w", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8))
          }

          for (w in 0..maxCapacity) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .width(44.dp)
                .height(38.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text("w=$w", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Table Rows
        for (i in 0..n) {
          val rowItem = if (i == 0) "Base 0" else "${items[i - 1].name} (w=${items[i-1].weight}, v=${items[i-1].value})"
          Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Row Label
            Box(
              modifier = Modifier
                .width(76.dp)
                .height(42.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                .padding(4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (i == 0) "0 (no items)" else "Item $i",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = Color(0xFFF1F5F9),
                textAlign = TextAlign.Center
              )
            }

            for (w in 0..maxCapacity) {
              Spacer(modifier = Modifier.width(4.dp))
              val cellIdx = i * (maxCapacity + 1) + w
              val isFilled = cellIdx < fillProgressCell
              val isSelected = selectedCell == Pair(i, w)
              val isTracebackCell = isTracebackMode && optimalPath.contains(Pair(i, w))

              val cellBg = when {
                isSelected -> Color(0xFFF59E0B) // Amber
                isTracebackCell -> Color(0xFF10B981) // Emerald Green
                isFilled -> Color(0xFF1E293B)
                else -> Color(0xFF090D16)
              }

              val textColor = when {
                isSelected -> Color.Black
                isTracebackCell -> Color.White
                isFilled -> Color(0xFF38BDF8)
                else -> Color(0xFF475569)
              }

              Box(
                modifier = Modifier
                  .width(44.dp)
                  .height(42.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(cellBg)
                  .border(
                    width = if (isSelected || isTracebackCell) 1.5.dp else 0.5.dp,
                    color = if (isSelected) Color.White else if (isTracebackCell) Color(0xFF34D399) else Color(0xFF334155),
                    shape = RoundedCornerShape(6.dp)
                  )
                  .clickable {
                    selectedCell = Pair(i, w)
                  },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (isFilled) "${dpTable[i][w]}" else "-",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                  ),
                  color = textColor
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Traceback Result Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Route, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Traceback Reconstruction Result:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          "Optimal Knapsack Value: ${dpTable[n][maxCapacity]}\n" +
              "Items Selected: Item 4 (w=5, v=8) + Item 1 (w=2, v=3) -> Total Weight = 7, Total Value = 11.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
