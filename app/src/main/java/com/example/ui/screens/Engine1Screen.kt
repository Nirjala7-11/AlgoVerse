package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AlgorithmType
import com.example.model.ArrayPreset
import com.example.model.StepActionType
import com.example.ui.components.CanvasSortingVisualizer
import com.example.ui.components.CodeSyncViewer
import com.example.ui.components.ComplexityIntuitionCard
import com.example.ui.components.CustomArrayInput
import com.example.ui.components.DualCompareCard
import com.example.ui.components.EnhancedPresetsCard
import com.example.ui.components.LiveCountersCard
import com.example.ui.components.PlaybackControlsCard
import com.example.ui.components.PostAlgoQuizSheet
import com.example.ui.components.PredictModeWidget
import com.example.ui.components.PredictPromptCard
import com.example.ui.components.PresetsRow
import com.example.ui.components.SwapCompareInspector
import com.example.ui.components.VisualizationEngine
import com.example.viewmodel.AlgoVerseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Engine1Screen(
  viewModel: AlgoVerseViewModel,
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit = {},
  onNavigateTopic: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val scrollState = rememberScrollState()
  var lastSelectedPreset by remember { mutableStateOf<ArrayPreset?>(null) }
  var selectedViewEngine by remember { mutableStateOf(0) } // 0: VisualizationEngine, 1: Physics Canvas & Compare Inspector

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    floatingActionButton = {
      val step = uiState.currentStep
      val isSwapping = step?.actionType == StepActionType.SWAPPING
      val isComparing = step?.actionType == StepActionType.COMPARING
      val willSwap = step?.predictWillSwapOrUpdate == true || (isComparing && step != null && step.primaryIndex >= 0 && step.secondaryIndex >= 0 && step.primaryIndex < uiState.currentArray.size && step.secondaryIndex < uiState.currentArray.size && uiState.currentArray[step.primaryIndex] > uiState.currentArray[step.secondaryIndex])

      ExtendedFloatingActionButton(
        onClick = {
          if (uiState.currentStepIndex >= uiState.steps.size - 1) {
            viewModel.reset()
          } else {
            viewModel.stepForward()
          }
        },
        icon = {
          Icon(
            imageVector = when {
              isSwapping || willSwap -> Icons.Default.SwapHoriz
              uiState.currentStepIndex >= uiState.steps.size - 1 -> Icons.Default.Refresh
              else -> Icons.AutoMirrored.Filled.ArrowForward
            },
            contentDescription = null
          )
        },
        text = {
          Text(
            text = when {
              isSwapping -> "Swap Completed ➔"
              willSwap -> "Execute Swap ⇄"
              isComparing -> "Next Compare ➔"
              uiState.currentStepIndex >= uiState.steps.size - 1 -> "Replay Sort"
              else -> "Next Step ➔"
            },
            fontWeight = FontWeight.Bold
          )
        },
        containerColor = when {
          isSwapping || willSwap -> Color(0xFFEF4444)
          isComparing -> Color(0xFFF59E0B)
          else -> MaterialTheme.colorScheme.primary
        },
        contentColor = Color.White,
        modifier = Modifier
          .navigationBarsPadding()
          .padding(bottom = 6.dp, end = 4.dp)
          .testTag("fab_step_swap_button")
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
          // Top bar brand row with Navigation
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = onBackToDashboard,
                modifier = Modifier
                  .size(40.dp)
                  .testTag("back_to_dashboard_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back to Learning Hub Dashboard",
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              IconButton(
                onClick = onOpenSidebar,
                modifier = Modifier
                  .size(40.dp)
                  .testTag("open_sidebar_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Menu,
                  contentDescription = "Open Topic Sidebar",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.width(4.dp))

              Column {
                Text(
                  text = "AlgoVerse",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = when (uiState.selectedTab) {
                    1 -> "Dual Algorithm Comparison"
                    2 -> "Complexity & Inversion Intuition"
                    else -> "Topic 1: Compare & Swap Sorting"
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              if (uiState.selectedTab == 0) {
                // Mastery Checkpoint Quiz Quick Button for Tab 0
                Button(
                  onClick = { viewModel.openQuiz() },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                  ),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("open_quiz_topbar_button")
                ) {
                  Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Quiz", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))
              } else if (uiState.selectedTab == 1) {
                // Dedicated Race Lab Shortcut for Dual Compare Tab
                Button(
                  onClick = { onNavigateTopic("compare_algorithms") },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                  ),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("open_race_lab_compare_button")
                ) {
                  Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Race Lab", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))
              }

              // Topics Quick Jump Button
              Button(
                onClick = onOpenSidebar,
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("topics_quick_jump")
              ) {
                Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Topics", fontWeight = FontWeight.Bold)
              }
            }
          }

          // Algorithm Selectors: Only show on Tab 0 (Visualizer) to avoid cluttering Dual Compare
          if (uiState.selectedTab == 0) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AlgorithmType.entries.forEach { algo ->
                val isSelected = uiState.currentAlgorithm == algo
                FilterChip(
                  selected = isSelected,
                  onClick = { viewModel.setAlgorithm(algo) },
                  label = {
                    Text(
                      text = algo.displayName,
                      style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                      )
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                  ),
                  modifier = Modifier
                    .height(38.dp)
                    .testTag("algo_chip_${algo.name.lowercase()}")
                )
              }
            }
          }

          // Secondary Navigation Tabs
          SecondaryTabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Tab(
              selected = uiState.selectedTab == 0,
              onClick = { viewModel.setSelectedTab(0) },
              text = { Text("Visualizer") },
              icon = { Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(18.dp)) },
              modifier = Modifier.testTag("tab_visualizer")
            )
            Tab(
              selected = uiState.selectedTab == 1,
              onClick = { viewModel.setSelectedTab(1) },
              text = { Text("Dual Compare") },
              icon = { Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(18.dp)) },
              modifier = Modifier.testTag("tab_dual_compare")
            )
            Tab(
              selected = uiState.selectedTab == 2,
              onClick = { viewModel.setSelectedTab(2) },
              text = { Text("Intuition") },
              icon = { Icon(Icons.Default.Functions, contentDescription = null, modifier = Modifier.size(18.dp)) },
              modifier = Modifier.testTag("tab_intuition")
            )
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
        .verticalScroll(scrollState)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      when (uiState.selectedTab) {
        0 -> {
          // Tab 0: Visualizer & Predict Mode
          // 1. Enhanced Rich Presets Selector (Cards with complexity badges & preview mini-bars)
          EnhancedPresetsCard(
            selectedPreset = lastSelectedPreset,
            onSelectPreset = { preset ->
              lastSelectedPreset = preset
              viewModel.selectPreset(preset)
            },
            modifier = Modifier.padding(bottom = 8.dp)
          )

          // One-click quick presets row positioned directly above CustomArrayInput
          PresetsRow(
            onSelectPreset = { preset ->
              lastSelectedPreset = preset
              viewModel.selectPreset(preset)
            },
            modifier = Modifier.padding(bottom = 6.dp)
          )

          // 2. Custom Array Input
          CustomArrayInput(
            rawInput = uiState.rawInputText,
            onInputChange = {
              lastSelectedPreset = null
              viewModel.onInputTextChanged(it)
            },
            onApply = { viewModel.applyCustomInput() },
            errorMessage = uiState.inputErrorMessage,
            modifier = Modifier.padding(bottom = 10.dp)
          )

          // Visualizer Mode Switcher Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = selectedViewEngine == 0,
              onClick = { selectedViewEngine = 0 },
              label = {
                Text(
                  "⚡ VisualizationEngine",
                  fontWeight = if (selectedViewEngine == 0) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("select_view_visualization_engine")
            )
            FilterChip(
              selected = selectedViewEngine == 1,
              onClick = { selectedViewEngine = 1 },
              label = {
                Text(
                  "🎨 Physics Canvas & Inspector",
                  fontWeight = if (selectedViewEngine == 1) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("select_view_physics_inspector")
            )
          }

          if (selectedViewEngine == 0) {
            // Reusable VisualizationEngine UI component with PredictModeWidget quiz overlay
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
            ) {
              VisualizationEngine(
                currentStep = uiState.currentStep,
                arrayFallback = uiState.currentArray,
                totalSteps = uiState.steps.size,
                currentStepIndex = uiState.currentStepIndex,
                isPlaying = uiState.isPlaying,
                playbackSpeedMs = uiState.playbackSpeedMs,
                onPlay = { viewModel.play() },
                onPause = { viewModel.pause() },
                onStepForward = { viewModel.stepForward() },
                onStepBackward = { viewModel.stepBackward() },
                onReset = { viewModel.reset() },
                onJumpToEnd = { viewModel.jumpToEnd() },
                onSeekTo = { viewModel.seekTo(it) },
                onSetSpeed = { viewModel.setPlaybackSpeed(it) },
                algorithmType = uiState.currentAlgorithm,
                modifier = Modifier.fillMaxWidth()
              )

              // Interactive Quiz-Style PredictModeWidget Overlay
              PredictModeWidget(
                currentStep = uiState.currentStep,
                isPredictModeActive = uiState.predictModeEnabled,
                isStepAnswered = uiState.answeredStepIndices.contains(uiState.currentStepIndex),
                feedback = uiState.activePredictionFeedback,
                currentStreak = uiState.currentStreak,
                accuracyPercentage = uiState.accuracyPercentage,
                onSubmitPrediction = { userChoseYes -> viewModel.submitPrediction(userChoseYes) },
                onContinuePlayback = { viewModel.dismissPredictionAndContinue() },
                modifier = Modifier
                  .align(Alignment.Center)
                  .padding(12.dp)
              )
            }

            // Post-Visualization Mastery Quiz Checkpoint Banner (Triggers upon sorting completion)
            if (uiState.isAtEnd) {
              PostVisualizationCheckpointBanner(
                algorithm = uiState.currentAlgorithm,
                onTakeQuiz = { viewModel.openQuiz() },
                modifier = Modifier.padding(bottom = 10.dp)
              )
            }

            // Predict Mode Card
            PredictPromptCard(
              predictModeEnabled = uiState.predictModeEnabled,
              onTogglePredictMode = { viewModel.togglePredictMode() },
              currentStep = uiState.currentStep,
              feedback = uiState.activePredictionFeedback,
              totalPredictions = uiState.totalPredictions,
              correctPredictions = uiState.correctPredictions,
              currentStreak = uiState.currentStreak,
              accuracyPercentage = uiState.accuracyPercentage,
              isStepAnswered = uiState.answeredStepIndices.contains(uiState.currentStepIndex),
              onSubmitPrediction = { userChoseYes -> viewModel.submitPrediction(userChoseYes) },
              onContinue = { viewModel.dismissPredictionAndContinue() },
              modifier = Modifier.padding(bottom = 10.dp)
            )

            // Interactive Pseudocode Execution Panel with Line Highlighting & Scope Watch
            CodeSyncViewer(
              algorithm = uiState.currentAlgorithm,
              currentStep = uiState.currentStep,
              onStepForward = { viewModel.stepForward() },
              onStepBackward = { viewModel.stepBackward() },
              onReset = { viewModel.reset() },
              modifier = Modifier.padding(bottom = 16.dp)
            )
          } else {
            // 3. Live Counters Card (Comparisons, Swaps, Dynamic Time Complexity Region)
            LiveCountersCard(
              algorithm = uiState.currentAlgorithm,
              currentStep = uiState.currentStep,
              currentRegion = uiState.currentComplexityRegion,
              complexityExplanation = uiState.complexityExplanation,
              arraySize = uiState.currentArray.size,
              modifier = Modifier.padding(bottom = 10.dp)
            )

            // 4. Canvas Bar Chart Visualizer with Physical Gliding Swap Animations
            CanvasSortingVisualizer(
              algorithm = uiState.currentAlgorithm,
              currentStep = uiState.currentStep,
              arrayFallback = uiState.currentArray,
              modifier = Modifier.padding(bottom = 10.dp)
            )

            // Post-Visualization Mastery Quiz Checkpoint Banner (Triggers upon sorting completion)
            if (uiState.isAtEnd) {
              PostVisualizationCheckpointBanner(
                algorithm = uiState.currentAlgorithm,
                onTakeQuiz = { viewModel.openQuiz() },
                modifier = Modifier.padding(bottom = 10.dp)
              )
            }

            // 5. Interactive Compare & Swap Inspector (visualizes how elements are compared & swapped)
            val step = uiState.currentStep
            if (step != null && (step.actionType == StepActionType.COMPARING || step.actionType == StepActionType.SWAPPING)) {
              SwapCompareInspector(
                algorithm = uiState.currentAlgorithm,
                currentStep = step,
                arrayFallback = uiState.currentArray,
                onExecuteStep = { viewModel.stepForward() },
                modifier = Modifier.padding(bottom = 10.dp)
              )
            }

            // 6. Predict Mode Card
            PredictPromptCard(
              predictModeEnabled = uiState.predictModeEnabled,
              onTogglePredictMode = { viewModel.togglePredictMode() },
              currentStep = uiState.currentStep,
              feedback = uiState.activePredictionFeedback,
              totalPredictions = uiState.totalPredictions,
              correctPredictions = uiState.correctPredictions,
              currentStreak = uiState.currentStreak,
              accuracyPercentage = uiState.accuracyPercentage,
              isStepAnswered = uiState.answeredStepIndices.contains(uiState.currentStepIndex),
              onSubmitPrediction = { userChoseYes -> viewModel.submitPrediction(userChoseYes) },
              onContinue = { viewModel.dismissPredictionAndContinue() },
              modifier = Modifier.padding(bottom = 10.dp)
            )

            // 7. Playback Controls Card
            PlaybackControlsCard(
              isPlaying = uiState.isPlaying,
              currentStepIndex = uiState.currentStepIndex,
              totalSteps = uiState.steps.size,
              playbackSpeedMs = uiState.playbackSpeedMs,
              onPlay = { viewModel.play() },
              onPause = { viewModel.pause() },
              onStepForward = { viewModel.stepForward() },
              onStepBackward = { viewModel.stepBackward() },
              onReset = { viewModel.reset() },
              onJumpToEnd = { viewModel.jumpToEnd() },
              onSeekTo = { viewModel.seekTo(it) },
              onSetSpeed = { viewModel.setPlaybackSpeed(it) },
              modifier = Modifier.padding(bottom = 10.dp)
            )

            // 8. Interactive Pseudocode Execution Panel with Line Highlighting & Scope Watch
            CodeSyncViewer(
              algorithm = uiState.currentAlgorithm,
              currentStep = uiState.currentStep,
              onStepForward = { viewModel.stepForward() },
              onStepBackward = { viewModel.stepBackward() },
              onReset = { viewModel.reset() },
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }
        }
        1 -> {
          // Tab 1: Dual Compare (Bubble Sort vs Selection Sort)
          EnhancedPresetsCard(
            selectedPreset = lastSelectedPreset,
            onSelectPreset = { preset ->
              lastSelectedPreset = preset
              viewModel.selectPreset(preset)
            },
            modifier = Modifier.padding(bottom = 10.dp)
          )

          CustomArrayInput(
            rawInput = uiState.rawInputText,
            onInputChange = {
              lastSelectedPreset = null
              viewModel.onInputTextChanged(it)
            },
            onApply = { viewModel.applyCustomInput() },
            errorMessage = uiState.inputErrorMessage,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          // Live Animated Side-by-Side Race Launcher Card
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp)
              .testTag("launch_comparative_race_banner")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "⚡ Real-Time Animated Race Lab",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Compare QuickSort vs MergeSort, BubbleSort & more with synchronized live animation!",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { onNavigateTopic("compare_algorithms") },
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("open_live_race_screen_button")
              ) {
                Text("Open Race ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          DualCompareCard(
            bubbleSteps = uiState.dualBubbleSteps,
            selectionSteps = uiState.dualSelectionSteps,
            arraySize = uiState.currentArray.size,
            modifier = Modifier.padding(bottom = 16.dp)
          )
        }
        2 -> {
          // Tab 2: Theory & Complexity Intuition
          ComplexityIntuitionCard(
            algorithm = uiState.currentAlgorithm,
            currentArrayInversions = uiState.currentStep?.remainingInversions ?: 0,
            arraySize = uiState.currentArray.size,
            modifier = Modifier.padding(bottom = 16.dp)
          )
        }
      }
    }

    if (uiState.isQuizSheetOpen) {
      PostAlgoQuizSheet(
        uiState = uiState,
        onDismiss = { viewModel.closeQuiz() },
        onSelectAnswer = { qId, optIdx -> viewModel.selectQuizAnswer(qId, optIdx) },
        onNextQuestion = { viewModel.nextQuizQuestion() },
        onRestartQuiz = { viewModel.openQuiz() },
        onNavigateToTopic = { topicId ->
          viewModel.closeQuiz()
          onNavigateTopic(topicId)
        }
      )
    }
  }
}

@Composable
private fun PostVisualizationCheckpointBanner(
  algorithm: AlgorithmType,
  onTakeQuiz: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.5.dp,
      MaterialTheme.colorScheme.primary
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("post_visualization_checkpoint_card")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Array Sorted! Mastery Quiz Ready 🎉",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "3 quick questions to reinforce learning and suggest your next path",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onTakeQuiz,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("open_post_quiz_banner_button")
      ) {
        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Take 3-Question Mastery Quiz ➔",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}
