package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
import kotlin.math.abs

data class DynamicQueenStep(
  val stepIndex: Int,
  val queens: List<Int>, // row index -> col index (-1 if unplaced)
  val isPruned: Boolean,
  val pruneReason: String,
  val isSolution: Boolean,
  val explanation: String
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun Engine6PruningScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // State: Board Size & Mode
  var boardSize by remember { mutableIntStateOf(4) }
  var modeTab by remember { mutableIntStateOf(0) } // 0: Manual Interactive Sandbox (Tap to Insert/Delete Queen), 1: Auto Backtracking Solver
  var currentStepIdx by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(false) }

  // Manual Sandbox Queens: map row -> col
  val manualQueens = remember { mutableStateListOf(-1, -1, -1, -1, -1, -1, -1, -1) }

  // Reset manual queens on board size change
  LaunchedEffect(boardSize) {
    for (i in 0 until 8) {
      manualQueens[i] = -1
    }
  }

  // Helper to test if two queens conflict
  fun hasConflict(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
    if (r1 == r2 || c1 == c2) return true
    if (abs(r1 - r2) == abs(c1 - c2)) return true
    return false
  }

  // Count conflicts in manual mode
  val manualConflicts = remember(manualQueens.toList(), boardSize) {
    val conflicts = mutableSetOf<Pair<Int, Int>>()
    for (r1 in 0 until boardSize) {
      val c1 = manualQueens[r1]
      if (c1 == -1) continue
      for (r2 in (r1 + 1) until boardSize) {
        val c2 = manualQueens[r2]
        if (c2 == -1) continue
        if (hasConflict(r1, c1, r2, c2)) {
          conflicts.add(Pair(r1, c1))
          conflicts.add(Pair(r2, c2))
        }
      }
    }
    conflicts
  }

  val placedQueenCount = (0 until boardSize).count { manualQueens[it] != -1 }
  val isManualSolution = placedQueenCount == boardSize && manualConflicts.isEmpty()

  // Dynamic Backtracking Solver Step Generator for N
  fun generateBacktrackingSteps(n: Int): List<DynamicQueenStep> {
    val steps = mutableListOf<DynamicQueenStep>()
    val queens = MutableList(n) { -1 }
    var prunedCount = 0
    var solutionCount = 0

    steps.add(
      DynamicQueenStep(
        stepIndex = 0,
        queens = queens.toList(),
        isPruned = false,
        pruneReason = "",
        isSolution = false,
        explanation = "Starting Backtracking on empty ${n}x${n} chessboard. Exploring Row 0."
      )
    )

    fun solve(row: Int) {
      if (row == n) {
        solutionCount++
        steps.add(
          DynamicQueenStep(
            stepIndex = steps.size,
            queens = queens.toList(),
            isPruned = false,
            pruneReason = "",
            isSolution = true,
            explanation = "🎉 SOLUTION $solutionCount FOUND! All $n Queens safely placed without conflicts: [${queens.joinToString(", ")}]."
          )
        )
        return
      }

      for (col in 0 until n) {
        // Tentatively place queen
        queens[row] = col
        var conflictReason: String? = null

        // Check conflicts against previous rows
        for (prevRow in 0 until row) {
          val prevCol = queens[prevRow]
          if (prevCol == col) {
            conflictReason = "Column collision: (Row $row, Col $col) attacks (Row $prevRow, Col $prevCol) in Col $col!"
            break
          }
          if (abs(row - prevRow) == abs(col - prevCol)) {
            conflictReason = "Diagonal collision: |$row - $prevRow| == |$col - $prevCol| = ${abs(row - prevRow)}!"
            break
          }
        }

        if (conflictReason != null) {
          prunedCount++
          steps.add(
            DynamicQueenStep(
              stepIndex = steps.size,
              queens = queens.toList(),
              isPruned = true,
              pruneReason = conflictReason,
              isSolution = false,
              explanation = "Pruned (Row $row, Col $col): $conflictReason Backtracking!"
            )
          )
          // Limit step explosion on large boards
          if (steps.size > 250) {
            queens[row] = -1
            return
          }
        } else {
          steps.add(
            DynamicQueenStep(
              stepIndex = steps.size,
              queens = queens.toList(),
              isPruned = false,
              pruneReason = "",
              isSolution = false,
              explanation = "Placed Q$row at ($row, $col). Position is non-attacking! Exploring Row ${row + 1}."
            )
          )
          solve(row + 1)
        }
        queens[row] = -1
      }
    }

    solve(0)
    return steps
  }

  val autoSteps = remember(boardSize) { generateBacktrackingSteps(boardSize) }
  val currentAutoStep = autoSteps[currentStepIdx.coerceIn(0, autoSteps.size - 1)]

  // Auto-play coroutine
  LaunchedEffect(isPlaying, currentStepIdx, autoSteps.size) {
    if (isPlaying) {
      if (currentStepIdx < autoSteps.size - 1) {
        delay(500L)
        currentStepIdx++
      } else {
        isPlaying = false
      }
    }
  }

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
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackToDashboard) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard", tint = MaterialTheme.colorScheme.primary)
          }
          Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Topic 6: N-Queens Pruning Studio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Interactive Insertion & Backtracking Trees", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Button(onClick = onOpenSidebar, modifier = Modifier.testTag("e6_sidebar_button")) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Board Size Selector (N = 4, 5, 6, 8)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Board Size (N):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(4, 5, 6, 8).forEach { size ->
          FilterChip(
            selected = boardSize == size,
            onClick = {
              boardSize = size
              currentStepIdx = 0
              isPlaying = false
            },
            label = { Text("${size}x$size") },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Mode Selector: Manual Sandbox vs Auto Solver
    SecondaryTabRow(
      selectedTabIndex = modeTab,
      containerColor = MaterialTheme.colorScheme.surface,
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = modeTab == 0,
        onClick = { modeTab = 0; isPlaying = false },
        text = { Text("Manual Sandbox (Tap to Place/Delete)") }
      )
      Tab(
        selected = modeTab == 1,
        onClick = { modeTab = 1 },
        text = { Text("Auto Backtracking Solver") }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (modeTab == 0) {
      // MODE 0: Manual Interactive Sandbox
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isManualSolution) "🎉 VALID SOLUTION! All $boardSize Queens safe!" else "Queens Placed: $placedQueenCount / $boardSize",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isManualSolution) Color(0xFF10B981) else if (manualConflicts.isNotEmpty()) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = if (manualConflicts.isNotEmpty()) "⚠️ Conflicts detected! Attacking queens shown in red." else "Tap any square to insert or delete a queen.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              for (i in 0 until 8) manualQueens[i] = -1
            }
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Clear Board", tint = Color(0xFF94A3B8))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Chessboard Grid (Interactive Tapping)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          val squareSize = when (boardSize) {
            8 -> 36.dp
            6 -> 46.dp
            else -> 54.dp
          }

          for (r in 0 until boardSize) {
            Row {
              for (c in 0 until boardSize) {
                val isDarkSquare = (r + c) % 2 == 1
                val hasQueen = manualQueens[r] == c
                val isConflicted = hasQueen && manualConflicts.contains(Pair(r, c))

                val squareColor = when {
                  isConflicted -> Color(0xFFDC2626) // Red conflict
                  hasQueen -> Color(0xFF10B981) // Green safe
                  isDarkSquare -> Color(0xFF1E293B)
                  else -> Color(0xFF334155)
                }

                Box(
                  modifier = Modifier
                    .size(squareSize)
                    .background(squareColor)
                    .border(0.5.dp, Color(0xFF0F172A))
                    .clickable {
                      // Toggle queen placement in row r
                      if (manualQueens[r] == c) {
                        manualQueens[r] = -1 // Delete queen
                      } else {
                        manualQueens[r] = c // Insert queen at (r, c)
                      }
                    },
                  contentAlignment = Alignment.Center
                ) {
                  if (hasQueen) {
                    Text(
                      text = "♕",
                      fontSize = if (boardSize == 8) 22.sp else 30.sp,
                      color = Color.White
                    )
                  }
                }
              }
            }
          }
        }
      }
    } else {
      // MODE 1: Auto Backtracking Solver
      // Step Banner
      if (currentAutoStep.isPruned) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF7F1D1D).copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Branch Pruned by Bounding Function:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5)))
              Text(currentAutoStep.pruneReason, style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      } else if (currentAutoStep.isSolution) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF065F46).copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("VALID COMPLETE STATE: Solution Confirmed! 🎉", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // Step Explanation
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = currentAutoStep.explanation,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
          color = Color.White,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Chessboard Grid
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          val squareSize = when (boardSize) {
            8 -> 36.dp
            6 -> 46.dp
            else -> 54.dp
          }

          for (r in 0 until boardSize) {
            Row {
              for (c in 0 until boardSize) {
                val isDarkSquare = (r + c) % 2 == 1
                val hasQueen = currentAutoStep.queens.getOrNull(r) == c
                val isConflict = hasQueen && currentAutoStep.isPruned

                val squareColor = when {
                  isConflict -> Color(0xFFDC2626) // Red conflict
                  hasQueen -> Color(0xFF10B981) // Green safe
                  isDarkSquare -> Color(0xFF1E293B)
                  else -> Color(0xFF334155)
                }

                Box(
                  modifier = Modifier
                    .size(squareSize)
                    .background(squareColor)
                    .border(0.5.dp, Color(0xFF0F172A)),
                  contentAlignment = Alignment.Center
                ) {
                  if (hasQueen) {
                    Text(
                      text = "♕",
                      fontSize = if (boardSize == 8) 22.sp else 30.sp,
                      color = Color.White
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Playback Controls
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { currentStepIdx = 0; isPlaying = false }) {
            Icon(Icons.Default.Replay, contentDescription = "Restart")
          }
          IconButton(onClick = { if (currentStepIdx > 0) currentStepIdx--; isPlaying = false }, enabled = currentStepIdx > 0) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
          }
          FilledIconButton(
            onClick = {
              if (currentStepIdx >= autoSteps.size - 1) {
                currentStepIdx = 0
                isPlaying = true
              } else {
                isPlaying = !isPlaying
              }
            }
          ) {
            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play/Pause")
          }
          IconButton(
            onClick = {
              if (currentStepIdx < autoSteps.size - 1) currentStepIdx++
              isPlaying = false
            },
            enabled = currentStepIdx < autoSteps.size - 1
          ) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next")
          }
          IconButton(onClick = { currentStepIdx = autoSteps.size - 1; isPlaying = false }) {
            Icon(Icons.Default.FastForward, contentDescription = "End")
          }
        }
      }
    }
  }
}
