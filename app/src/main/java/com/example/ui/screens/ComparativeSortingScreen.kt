package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.engine.ArraySortEngine
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlinx.coroutines.delay
import kotlin.random.Random

data class MatchupPreset(
  val title: String,
  val subtitle: String,
  val algoA: AlgorithmType,
  val algoB: AlgorithmType
)

val MATCHUP_PRESETS = listOf(
  MatchupPreset("QuickSort vs MergeSort", "Divide & Conquer Showdown", AlgorithmType.QUICK_SORT, AlgorithmType.MERGE_SORT),
  MatchupPreset("HeapSort vs QuickSort", "In-Place O(n log n) Battle", AlgorithmType.HEAP_SORT, AlgorithmType.QUICK_SORT),
  MatchupPreset("HeapSort vs MergeSort", "O(1) Aux Space vs O(n) Aux Writes", AlgorithmType.HEAP_SORT, AlgorithmType.MERGE_SORT),
  MatchupPreset("MergeSort vs BubbleSort", "O(n log n) vs O(n²)", AlgorithmType.MERGE_SORT, AlgorithmType.BUBBLE_SORT),
  MatchupPreset("QuickSort vs SelectionSort", "Pivot Partition vs Min Scan", AlgorithmType.QUICK_SORT, AlgorithmType.SELECTION_SORT),
  MatchupPreset("InsertionSort vs BubbleSort", "Adaptive In-Place Battle", AlgorithmType.INSERTION_SORT, AlgorithmType.BUBBLE_SORT)
)

/**
 * Side-by-Side Comparative Visualization Screen.
 * Allows users to select any two algorithms (e.g., QuickSort vs MergeSort)
 * and animates them simultaneously sorting the EXACT SAME randomly generated dataset
 * to highlight stark performance differences in comparisons, writes, and Big-O efficiency.
 */
@Composable
fun ComparativeSortingScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackToDashboard() }

  // State: Algorithm Selection
  var selectedAlgoA by remember { mutableStateOf(AlgorithmType.QUICK_SORT) }
  var selectedAlgoB by remember { mutableStateOf(AlgorithmType.MERGE_SORT) }

  // State: Dataset Configuration
  var datasetSize by remember { mutableIntStateOf(12) }
  var datasetType by remember { mutableStateOf("Random") }
  var sharedDataset by remember { mutableStateOf(generateComparativeDataset(12, "Random")) }

  // State: Step sequences generated from identical dataset
  var stepsA by remember { mutableStateOf(ArraySortEngine.generateSteps(selectedAlgoA, sharedDataset)) }
  var stepsB by remember { mutableStateOf(ArraySortEngine.generateSteps(selectedAlgoB, sharedDataset)) }

  // State: Playback pointers and execution
  var stepIndexA by remember { mutableIntStateOf(0) }
  var stepIndexB by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(false) }
  var speedMs by remember { mutableLongStateOf(200L) }

  // Winners tracking
  var firstPlaceAlgo by remember { mutableStateOf<AlgorithmType?>(null) }
  var secondPlaceAlgo by remember { mutableStateOf<AlgorithmType?>(null) }

  // Helper to re-generate both algorithms with identical dataset
  fun syncDataset(newArray: List<Int>) {
    isPlaying = false
    sharedDataset = newArray
    stepsA = ArraySortEngine.generateSteps(selectedAlgoA, newArray)
    stepsB = ArraySortEngine.generateSteps(selectedAlgoB, newArray)
    stepIndexA = 0
    stepIndexB = 0
    firstPlaceAlgo = null
    secondPlaceAlgo = null
  }

  // Interactive scrubber seek
  fun seekToFraction(fraction: Float) {
    isPlaying = false
    val targetA = ((stepsA.size - 1) * fraction).toInt().coerceIn(0, (stepsA.size - 1).coerceAtLeast(0))
    val targetB = ((stepsB.size - 1) * fraction).toInt().coerceIn(0, (stepsB.size - 1).coerceAtLeast(0))
    stepIndexA = targetA
    stepIndexB = targetB
    if (targetA >= stepsA.size - 1 && firstPlaceAlgo == null) firstPlaceAlgo = selectedAlgoA
    if (targetB >= stepsB.size - 1 && firstPlaceAlgo == null) firstPlaceAlgo = selectedAlgoB
  }

  // Helper when algorithms change
  fun onAlgorithmAChanged(newAlgo: AlgorithmType) {
    selectedAlgoA = newAlgo
    stepsA = ArraySortEngine.generateSteps(newAlgo, sharedDataset)
    stepIndexA = 0
    firstPlaceAlgo = null
    secondPlaceAlgo = null
  }

  fun onAlgorithmBChanged(newAlgo: AlgorithmType) {
    selectedAlgoB = newAlgo
    stepsB = ArraySortEngine.generateSteps(newAlgo, sharedDataset)
    stepIndexB = 0
    firstPlaceAlgo = null
    secondPlaceAlgo = null
  }

  // Playback engine coroutine
  LaunchedEffect(isPlaying, speedMs, stepIndexA, stepIndexB, stepsA.size, stepsB.size) {
    if (isPlaying) {
      val isAComplete = stepsA.isNotEmpty() && stepIndexA >= stepsA.size - 1
      val isBComplete = stepsB.isNotEmpty() && stepIndexB >= stepsB.size - 1

      // If both completed, stop playing
      if (isAComplete && isBComplete) {
        isPlaying = false
      } else {
        delay(speedMs)

        // Advance A if not complete
        if (!isAComplete) {
          val nextA = stepIndexA + 1
          stepIndexA = nextA
          if (nextA >= stepsA.size - 1 && firstPlaceAlgo == null) {
            firstPlaceAlgo = selectedAlgoA
          }
        }

        // Advance B if not complete
        if (!isBComplete) {
          val nextB = stepIndexB + 1
          stepIndexB = nextB
          if (nextB >= stepsB.size - 1 && firstPlaceAlgo == null) {
            firstPlaceAlgo = selectedAlgoB
          }
        }

        // Check 2nd place
        if (firstPlaceAlgo != null && secondPlaceAlgo == null) {
          if (firstPlaceAlgo == selectedAlgoA && stepsB.isNotEmpty() && stepIndexB >= stepsB.size - 1) {
            secondPlaceAlgo = selectedAlgoB
          } else if (firstPlaceAlgo == selectedAlgoB && stepsA.isNotEmpty() && stepIndexA >= stepsA.size - 1) {
            secondPlaceAlgo = selectedAlgoA
          }
        }
      }
    }
  }

  val currentStepA = if (stepsA.isNotEmpty() && stepIndexA in stepsA.indices) stepsA[stepIndexA] else null
  val currentStepB = if (stepsB.isNotEmpty() && stepIndexB in stepsB.indices) stepsB[stepIndexB] else null

  val isBothFinished = (stepsA.isNotEmpty() && stepIndexA >= stepsA.size - 1) &&
    (stepsB.isNotEmpty() && stepIndexB >= stepsB.size - 1)

  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onOpenSidebar,
              modifier = Modifier.testTag("nav_open_sidebar_compare")
            ) {
              Icon(Icons.Default.Menu, contentDescription = "Menu")
            }

            IconButton(
              onClick = onBackToDashboard,
              modifier = Modifier.testTag("nav_back_to_dashboard_compare")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column {
              Text(
                text = "Head-to-Head Race",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Side-by-Side Comparative Animation",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          // Dataset quick reroll button
          FilledTonalButton(
            onClick = {
              syncDataset(generateComparativeDataset(datasetSize, datasetType))
            },
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("compare_reroll_top_button")
          ) {
            Icon(Icons.Default.Shuffle, contentDescription = "Re-roll Array", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Re-roll", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(WindowInsets.navigationBars.asPaddingValues())
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      // 1. Matchup Selector & Preset Battle Deck
      AlgorithmMatchupPickerCard(
        algoA = selectedAlgoA,
        algoB = selectedAlgoB,
        onSelectAlgoA = { onAlgorithmAChanged(it) },
        onSelectAlgoB = { onAlgorithmBChanged(it) },
        onSelectPreset = { preset ->
          selectedAlgoA = preset.algoA
          selectedAlgoB = preset.algoB
          stepsA = ArraySortEngine.generateSteps(preset.algoA, sharedDataset)
          stepsB = ArraySortEngine.generateSteps(preset.algoB, sharedDataset)
          stepIndexA = 0
          stepIndexB = 0
          isPlaying = false
          firstPlaceAlgo = null
          secondPlaceAlgo = null
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Shared Dataset Guarantee Strip & Size Controls
      SharedDatasetControllerCard(
        currentDataset = sharedDataset,
        datasetSize = datasetSize,
        datasetType = datasetType,
        onSizeChanged = { size ->
          datasetSize = size
          syncDataset(generateComparativeDataset(size, datasetType))
        },
        onTypeChanged = { type ->
          datasetType = type
          syncDataset(generateComparativeDataset(datasetSize, type))
        },
        onRegenerate = {
          syncDataset(generateComparativeDataset(datasetSize, datasetType))
        }
      )

      Spacer(modifier = Modifier.height(12.dp))

      val progressA = if (stepsA.size > 1) (stepIndexA.toFloat() / (stepsA.size - 1).toFloat()).coerceIn(0f, 1f) else 0f
      val progressB = if (stepsB.size > 1) (stepIndexB.toFloat() / (stepsB.size - 1).toFloat()).coerceIn(0f, 1f) else 0f
      val maxProgress = maxOf(progressA, progressB)

      // 3. Unified Synchronized Playback Control Deck with Scrubbing Slider
      ComparativePlaybackControls(
        isPlaying = isPlaying,
        speedMs = speedMs,
        maxProgress = maxProgress,
        onSeekFraction = { seekToFraction(it) },
        canStepForward = !isBothFinished,
        canStepBackward = stepIndexA > 0 || stepIndexB > 0,
        onTogglePlay = { isPlaying = !isPlaying },
        onStepForward = {
          isPlaying = false
          if (stepIndexA < stepsA.size - 1) stepIndexA++
          if (stepIndexB < stepsB.size - 1) stepIndexB++
        },
        onStepBackward = {
          isPlaying = false
          if (stepIndexA > 0) stepIndexA--
          if (stepIndexB > 0) stepIndexB--
        },
        onReset = {
          isPlaying = false
          stepIndexA = 0
          stepIndexB = 0
          firstPlaceAlgo = null
          secondPlaceAlgo = null
        },
        onSpeedChanged = { speedMs = it }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Golden Winner Announcement Banner
      AnimatedVisibility(
        visible = firstPlaceAlgo != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFFEF3C7),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .testTag("winner_celebration_banner")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = Color(0xFFF59E0B),
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.EmojiEvents,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "🏆 ${firstPlaceAlgo?.displayName} Finished in 1st Place!",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF92400E)
                )
              )
              val winnerSteps = if (firstPlaceAlgo == selectedAlgoA) stepsA.size else stepsB.size
              val otherAlgo = if (firstPlaceAlgo == selectedAlgoA) selectedAlgoB else selectedAlgoA
              val otherSteps = if (firstPlaceAlgo == selectedAlgoA) stepsB.size else stepsA.size
              val otherCur = if (firstPlaceAlgo == selectedAlgoA) stepIndexB else stepIndexA
              val otherPct = if (otherSteps > 0) ((otherCur.toFloat() / otherSteps.toFloat()) * 100).toInt() else 0

              Text(
                text = if (isBothFinished) {
                  "Race finished! ${firstPlaceAlgo?.displayName} sorted in $winnerSteps steps vs ${otherAlgo.displayName} in $otherSteps steps."
                } else {
                  "Completed in $winnerSteps steps! ${otherAlgo.displayName} is currently at $otherPct%."
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFFB45309))
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 4. Side-by-Side Comparative Canvas Cards
      BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth >= 650.dp

        if (isWide) {
          // Dual Column layout for Tablets / Landscape
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            ComparativeAlgorithmPanel(
              algorithm = selectedAlgoA,
              currentStep = currentStepA,
              stepIndex = stepIndexA,
              totalSteps = stepsA.size,
              accentColor = Color(0xFF0284C7), // Blue
              isWinner = firstPlaceAlgo == selectedAlgoA,
              isRunnerUp = secondPlaceAlgo == selectedAlgoA,
              isFinished = stepsA.isNotEmpty() && stepIndexA >= stepsA.size - 1,
              modifier = Modifier.weight(1f)
            )

            ComparativeAlgorithmPanel(
              algorithm = selectedAlgoB,
              currentStep = currentStepB,
              stepIndex = stepIndexB,
              totalSteps = stepsB.size,
              accentColor = Color(0xFF10B981), // Emerald
              isWinner = firstPlaceAlgo == selectedAlgoB,
              isRunnerUp = secondPlaceAlgo == selectedAlgoB,
              isFinished = stepsB.isNotEmpty() && stepIndexB >= stepsB.size - 1,
              modifier = Modifier.weight(1f)
            )
          }
        } else {
          // Stacked layout for Portrait Mobile
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            ComparativeAlgorithmPanel(
              algorithm = selectedAlgoA,
              currentStep = currentStepA,
              stepIndex = stepIndexA,
              totalSteps = stepsA.size,
              accentColor = Color(0xFF0284C7),
              isWinner = firstPlaceAlgo == selectedAlgoA,
              isRunnerUp = secondPlaceAlgo == selectedAlgoA,
              isFinished = stepsA.isNotEmpty() && stepIndexA >= stepsA.size - 1,
              modifier = Modifier.fillMaxWidth()
            )

            ComparativeAlgorithmPanel(
              algorithm = selectedAlgoB,
              currentStep = currentStepB,
              stepIndex = stepIndexB,
              totalSteps = stepsB.size,
              accentColor = Color(0xFF10B981),
              isWinner = firstPlaceAlgo == selectedAlgoB,
              isRunnerUp = secondPlaceAlgo == selectedAlgoB,
              isFinished = stepsB.isNotEmpty() && stepIndexB >= stepsB.size - 1,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5. Head-to-Head Performance Difference Breakdown
      PerformanceDifferenceCard(
        algoA = selectedAlgoA,
        algoB = selectedAlgoB,
        stepA = currentStepA,
        stepB = currentStepB,
        totalStepsA = stepsA.size,
        totalStepsB = stepsB.size,
        isFinished = isBothFinished
      )

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}

/**
 * Matchup selector card letting users pick any 2 algorithms and quick battle presets.
 */
@Composable
private fun AlgorithmMatchupPickerCard(
  algoA: AlgorithmType,
  algoB: AlgorithmType,
  onSelectAlgoA: (AlgorithmType) -> Unit,
  onSelectAlgoB: (AlgorithmType) -> Unit,
  onSelectPreset: (MatchupPreset) -> Unit
) {
  var showMenuA by remember { mutableStateOf(false) }
  var showMenuB by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("algorithm_matchup_picker_card")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(30.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Select Competitor Algorithms",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
          Text(
            text = "HEAD-TO-HEAD",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dual Dropdown Pickers with VS Badge in Middle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Algorithm A Dropdown Button
        Box(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0284C7).copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0284C7)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showMenuA = true }
              .testTag("picker_algo_a")
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
              Text(
                text = "ALGORITHM A",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0284C7)
                )
              )
              Text(
                text = algoA.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${algoA.averageCase} • Space: ${algoA.spaceComplexity}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          DropdownMenu(
            expanded = showMenuA,
            onDismissRequest = { showMenuA = false }
          ) {
            AlgorithmType.entries.forEach { type ->
              DropdownMenuItem(
                text = {
                  Column {
                    Text(type.displayName, fontWeight = FontWeight.Bold)
                    Text("${type.averageCase} Avg • Space ${type.spaceComplexity}", fontSize = 11.sp, color = Color.Gray)
                  }
                },
                onClick = {
                  showMenuA = false
                  onSelectAlgoA(type)
                }
              )
            }
          }
        }

        // Glowing VS Badge
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(34.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "VS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 11.sp
              )
            )
          }
        }

        // Algorithm B Dropdown Button
        Box(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF10B981).copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showMenuB = true }
              .testTag("picker_algo_b")
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
              Text(
                text = "ALGORITHM B",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF10B981)
                )
              )
              Text(
                text = algoB.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${algoB.averageCase} • Space: ${algoB.spaceComplexity}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          DropdownMenu(
            expanded = showMenuB,
            onDismissRequest = { showMenuB = false }
          ) {
            AlgorithmType.entries.forEach { type ->
              DropdownMenuItem(
                text = {
                  Column {
                    Text(type.displayName, fontWeight = FontWeight.Bold)
                    Text("${type.averageCase} Avg • Space ${type.spaceComplexity}", fontSize = 11.sp, color = Color.Gray)
                  }
                },
                onClick = {
                  showMenuB = false
                  onSelectAlgoB(type)
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Battle Matchup Chips
      Text(
        text = "Popular Showdown Matchups:",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        MATCHUP_PRESETS.forEach { preset ->
          val isSelected = algoA == preset.algoA && algoB == preset.algoB
          FilterChip(
            selected = isSelected,
            onClick = { onSelectPreset(preset) },
            label = { Text(preset.title, fontSize = 10.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.height(28.dp)
          )
        }
      }
    }
  }
}

/**
 * Shared Dataset Controller Guaranteeing Both Algorithms receive the exact same array.
 */
@Composable
private fun SharedDatasetControllerCard(
  currentDataset: List<Int>,
  datasetSize: Int,
  datasetType: String,
  onSizeChanged: (Int) -> Unit,
  onTypeChanged: (String) -> Unit,
  onRegenerate: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Shared Dataset Guarantee",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        OutlinedButton(
          onClick = onRegenerate,
          modifier = Modifier
            .height(28.dp)
            .testTag("regenerate_comparative_dataset_button"),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Generate New", fontSize = 10.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Array Preview Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        currentDataset.forEachIndexed { idx, value ->
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
          ) {
            Text(
              text = "$value",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Configuration controls: Size and Distribution
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Size Selector Chips
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Size:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          listOf(8, 12, 16, 24).forEach { size ->
            val isSelected = datasetSize == size
            FilterChip(
              selected = isSelected,
              onClick = { onSizeChanged(size) },
              label = { Text("$size", fontSize = 9.sp) },
              modifier = Modifier.height(24.dp)
            )
          }
        }

        // Distribution Type Selector Chips
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Type:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          listOf("Random", "Reversed", "Nearly Sorted").forEach { type ->
            val isSelected = datasetType == type
            FilterChip(
              selected = isSelected,
              onClick = { onTypeChanged(type) },
              label = { Text(type, fontSize = 9.sp) },
              modifier = Modifier.height(24.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Synchronized Playback Control Toolbar.
 */
@Composable
private fun ComparativePlaybackControls(
  isPlaying: Boolean,
  speedMs: Long,
  maxProgress: Float,
  onSeekFraction: (Float) -> Unit,
  canStepForward: Boolean,
  canStepBackward: Boolean,
  onTogglePlay: () -> Unit,
  onStepForward: () -> Unit,
  onStepBackward: () -> Unit,
  onReset: () -> Unit,
  onSpeedChanged: (Long) -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
      // Synchronized Scrubber Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Sync Scrubber (Drag to compare phase)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        Text(
          text = "${(maxProgress * 100).toInt()}% Done",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
      }

      Slider(
        value = maxProgress,
        onValueChange = onSeekFraction,
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp),
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary,
          inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Button controls row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Step Backward
        IconButton(
          onClick = onStepBackward,
          enabled = canStepBackward,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.SkipPrevious, contentDescription = "Step back")
        }

        // Main Play / Pause Button
        Button(
          onClick = onTogglePlay,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .height(38.dp)
            .testTag("compare_play_pause_button")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isPlaying) "Pause Race" else "Start Race (Both)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
        }

        // Step Forward
        IconButton(
          onClick = onStepForward,
          enabled = canStepForward,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.SkipNext, contentDescription = "Step forward")
        }

        // Reset
        IconButton(
          onClick = onReset,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset race")
        }

        // Speed Selector Chips
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          listOf(
            400L to "0.5x",
            200L to "1x",
            80L to "2.5x",
            20L to "Turbo"
          ).forEach { (ms, label) ->
            val isSelected = speedMs == ms
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier
                .clickable { onSpeedChanged(ms) }
                .padding(1.dp)
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Individual algorithm visualizer panel displaying the real-time sorting bars,
 * active comparison indices, comparisons, writes, and finish place.
 */
@Composable
private fun ComparativeAlgorithmPanel(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  stepIndex: Int,
  totalSteps: Int,
  accentColor: Color,
  isWinner: Boolean,
  isRunnerUp: Boolean,
  isFinished: Boolean,
  modifier: Modifier = Modifier
) {
  val array = currentStep?.arrayState ?: emptyList()
  val primaryIdx = currentStep?.primaryIndex ?: -1
  val secondaryIdx = currentStep?.secondaryIndex ?: -1
  val sortedIndices = currentStep?.sortedIndices ?: emptySet()
  val maxVal = remember(array) { (array.maxOrNull() ?: 100).coerceAtLeast(1) }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isWinner) 2.dp else 1.dp,
      color = if (isWinner) Color(0xFFF59E0B) else accentColor.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isWinner) 4.dp else 2.dp),
    modifier = modifier.testTag("comparative_panel_${algorithm.name.lowercase()}")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Panel Header: Algorithm name & finish badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = accentColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = algorithm.displayName,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = algorithm.averageCase,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }

        // Finish status pill
        when {
          isWinner -> {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFF59E0B),
              modifier = Modifier.testTag("winner_badge_${algorithm.name.lowercase()}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.EmojiEvents,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "1st Place Winner! 🏆",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
          isRunnerUp -> {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF94A3B8)
            ) {
              Text(
                text = "2nd Place",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
          isFinished -> {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF10B981)
            ) {
              Text(
                text = "Finished ✓",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
          else -> {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = accentColor.copy(alpha = 0.12f)
            ) {
              Text(
                text = "Step $stepIndex / $totalSteps",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = accentColor
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Animated Bar Canvas
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.Bottom
        ) {
          array.forEachIndexed { idx, value ->
            val isPrimary = idx == primaryIdx
            val isSecondary = idx == secondaryIdx
            val isSorted = sortedIndices.contains(idx)

            val barColor = when {
              isPrimary && currentStep?.actionType == StepActionType.SWAPPING -> Color(0xFFEF4444) // Red for swap
              isSecondary && currentStep?.actionType == StepActionType.SWAPPING -> Color(0xFFEF4444)
              isPrimary || isSecondary -> Color(0xFFF59E0B) // Amber for comparison
              isSorted -> Color(0xFF10B981) // Green for sorted
              else -> accentColor.copy(alpha = 0.85f)
            }

            val heightFraction = (value.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f)

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier.fillMaxHeight()
            ) {
              // Value label on bar
              Text(
                text = "$value",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )

              Spacer(modifier = Modifier.height(2.dp))

              Box(
                modifier = Modifier
                  .width(14.dp)
                  .fillMaxHeight(heightFraction)
                  .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                  .background(barColor)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Progress Bar
      val progress = if (totalSteps > 0) (stepIndex.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f) else 0f
      LinearProgressIndicator(
        progress = { progress },
        color = accentColor,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Live Counters Strip: Comparisons, Swaps/Writes, Action Type
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Comparisons",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${currentStep?.comparisonsCount ?: 0}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFFF59E0B)
            )
          )
        }

        Column {
          Text(
            text = if (algorithm == AlgorithmType.MERGE_SORT) "Aux Writes" else "Swaps",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${currentStep?.swapsCount ?: 0}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFFEF4444)
            )
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Action",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = currentStep?.actionType?.name ?: "START",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = accentColor
            )
          )
        }
      }
    }
  }
}

/**
 * Performance difference analysis card highlighting comparison delta,
 * writes delta, and Big-O efficiency insights.
 */
@Composable
private fun PerformanceDifferenceCard(
  algoA: AlgorithmType,
  algoB: AlgorithmType,
  stepA: SortStep?,
  stepB: SortStep?,
  totalStepsA: Int,
  totalStepsB: Int,
  isFinished: Boolean
) {
  val compA = stepA?.comparisonsCount ?: 0
  val compB = stepB?.comparisonsCount ?: 0
  val swapsA = stepA?.swapsCount ?: 0
  val swapsB = stepB?.swapsCount ?: 0

  val compDelta = kotlin.math.abs(compA - compB)
  val compWinner = if (compA <= compB) algoA else algoB
  val compLoser = if (compA <= compB) algoB else algoA
  val compSavedPercent = if (maxOf(compA, compB) > 0) {
    ((compDelta.toFloat() / maxOf(compA, compB).toFloat()) * 100).toInt()
  } else 0

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("performance_difference_card")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Performance Metric Difference",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        if (isFinished) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF10B981)
          ) {
            Text(
              text = "RACE COMPLETED",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 9.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Comparison Delta Banner
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (compDelta > 0) {
                "${compWinner.displayName} executed $compSavedPercent% fewer comparisons ($compDelta less than ${compLoser.displayName})"
              } else {
                "Both algorithms are currently tied in comparison operations ($compA comparisons each)"
              },
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${algoA.displayName}: $compA comparisons • ${algoB.displayName}: $compB comparisons",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Head-to-Head Table
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          ComparativeMetricRow("Average Time Complexity", algoA.averageCase, algoB.averageCase)
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
          ComparativeMetricRow("Memory / Aux Space", algoA.spaceComplexity, algoB.spaceComplexity)
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
          ComparativeMetricRow("Comparisons Recorded", "$compA", "$compB")
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
          ComparativeMetricRow("Swaps / Writes Recorded", "$swapsA", "$swapsB")
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
          ComparativeMetricRow("Total Animation Steps", "$totalStepsA", "$totalStepsB")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Conceptual takeaway
      Text(
        text = "💡 Theoretical Intuition: Divide & Conquer algorithms like ${if (algoA.averageCase.contains("log")) algoA.displayName else algoB.displayName} divide problem size recursively (T(n) = 2T(n/2) + O(n)), requiring far fewer element-to-element comparisons than quadratic O(n²) adjacent scans.",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun ComparativeMetricRow(
  metric: String,
  valA: String,
  valB: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = metric,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1.2f)
    )

    Text(
      text = valA,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFF0284C7)
      ),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(0.9f)
    )

    Text(
      text = "vs",
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = Color.Gray),
      textAlign = TextAlign.Center,
      modifier = Modifier.width(20.dp)
    )

    Text(
      text = valB,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFF10B981)
      ),
      textAlign = TextAlign.End,
      modifier = Modifier.weight(0.9f)
    )
  }
}

private fun generateComparativeDataset(size: Int, type: String): List<Int> {
  return when (type) {
    "Reversed" -> (size downTo 1).map { it * 7 }
    "Nearly Sorted" -> {
      val list = (1..size).map { it * 7 }.toMutableList()
      if (list.size >= 4) {
        val tmp = list[1]
        list[1] = list[2]
        list[2] = tmp
      }
      list
    }
    "Duplicates" -> {
      val candidates = listOf(10, 25, 40, 60)
      (1..size).map { candidates.random() }
    }
    else -> {
      val set = mutableSetOf<Int>()
      while (set.size < size) {
        set.add(Random.nextInt(5, 96))
      }
      set.toList()
    }
  }
}
