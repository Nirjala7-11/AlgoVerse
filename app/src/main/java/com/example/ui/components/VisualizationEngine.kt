package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Controller managing algorithm visualization playback, stepping, seeking, and speed.
 */
@Stable
class VisualizationEngineController(
  initialSteps: List<SortStep> = emptyList(),
  initialSpeedMs: Long = 400L,
  private val coroutineScope: CoroutineScope
) {
  var steps by mutableStateOf(initialSteps)
    private set

  var currentStepIndex by mutableIntStateOf(0)
    private set

  var isPlaying by mutableStateOf(false)
    private set

  var playbackSpeedMs by mutableLongStateOf(initialSpeedMs)
    private set

  var isLooping by mutableStateOf(false)
    private set

  val currentStep: SortStep?
    get() = steps.getOrNull(currentStepIndex)

  val totalSteps: Int
    get() = steps.size

  val isAtStart: Boolean
    get() = currentStepIndex <= 0

  val isAtEnd: Boolean
    get() = totalSteps > 0 && currentStepIndex >= totalSteps - 1

  val progress: Float
    get() = if (totalSteps > 1) {
      (currentStepIndex.toFloat() / (totalSteps - 1)).coerceIn(0f, 1f)
    } else 0f

  private var playJob: Job? = null

  fun setSteps(newSteps: List<SortStep>, resetPosition: Boolean = true) {
    pause()
    steps = newSteps
    if (resetPosition) {
      currentStepIndex = 0
    } else {
      currentStepIndex = currentStepIndex.coerceIn(0, (newSteps.size - 1).coerceAtLeast(0))
    }
  }

  fun play() {
    if (steps.isEmpty()) return
    if (isAtEnd) {
      currentStepIndex = 0
    }
    isPlaying = true
    startPlaybackLoop()
  }

  fun pause() {
    isPlaying = false
    playJob?.cancel()
    playJob = null
  }

  fun togglePlay() {
    if (isPlaying) pause() else play()
  }

  fun stepForward() {
    pause()
    if (currentStepIndex < steps.size - 1) {
      currentStepIndex++
    }
  }

  fun stepBackward() {
    pause()
    if (currentStepIndex > 0) {
      currentStepIndex--
    }
  }

  fun reset() {
    pause()
    currentStepIndex = 0
  }

  fun jumpToEnd() {
    pause()
    if (steps.isNotEmpty()) {
      currentStepIndex = steps.size - 1
    }
  }

  fun seekTo(index: Int) {
    if (steps.isEmpty()) return
    currentStepIndex = index.coerceIn(0, steps.size - 1)
  }

  fun setPlaybackSpeed(speedMs: Long) {
    playbackSpeedMs = speedMs
    if (isPlaying) {
      // Restart loop with new speed
      startPlaybackLoop()
    }
  }

  fun setLoop(loop: Boolean) {
    isLooping = loop
  }

  private fun startPlaybackLoop() {
    playJob?.cancel()
    playJob = coroutineScope.launch {
      while (isActive && isPlaying) {
        delay(playbackSpeedMs)
        if (currentStepIndex < steps.size - 1) {
          currentStepIndex++
        } else {
          if (isLooping) {
            delay(playbackSpeedMs)
            currentStepIndex = 0
          } else {
            isPlaying = false
            break
          }
        }
      }
    }
  }

  fun release() {
    pause()
  }
}

/**
 * Creates and remembers a [VisualizationEngineController] instance across recompositions.
 */
@Composable
fun rememberVisualizationEngineController(
  initialSteps: List<SortStep> = emptyList(),
  initialSpeedMs: Long = 400L
): VisualizationEngineController {
  val scope = rememberCoroutineScope()
  val controller = remember {
    VisualizationEngineController(
      initialSteps = initialSteps,
      initialSpeedMs = initialSpeedMs,
      coroutineScope = scope
    )
  }

  LaunchedEffect(initialSteps) {
    if (initialSteps != controller.steps) {
      controller.setSteps(initialSteps, resetPosition = false)
    }
  }

  DisposableEffect(controller) {
    onDispose {
      controller.release()
    }
  }

  return controller
}

/**
 * Reusable 'VisualizationEngine' UI component in Jetpack Compose that renders
 * array-based sorting algorithm steps with smooth element animations, highlighted compare/swap states,
 * live metrics HUD, and an interactive animation controller.
 */
@Composable
fun VisualizationEngine(
  controller: VisualizationEngineController,
  modifier: Modifier = Modifier,
  title: String? = null,
  algorithmType: AlgorithmType? = null,
  fallbackArray: List<Int> = emptyList(),
  showControls: Boolean = true,
  showMetrics: Boolean = true,
  showExplanation: Boolean = true,
  showPointers: Boolean = true,
  barHeight: Dp = 190.dp,
  onStepSelected: ((SortStep) -> Unit)? = null
) {
  val currentStep = controller.currentStep
  val arrayState = currentStep?.arrayState ?: fallbackArray
  val totalSteps = controller.totalSteps
  val currentStepIndex = controller.currentStepIndex
  val isPlaying = controller.isPlaying
  val playbackSpeedMs = controller.playbackSpeedMs

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("visualization_engine"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: Title & Algorithm Badge & Progress
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          if (!title.isNullOrBlank()) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          } else if (algorithmType != null) {
            Text(
              text = algorithmType.displayName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          } else {
            Text(
              text = "Array Visualization Engine",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          if (algorithmType != null) {
            Text(
              text = "Time: ${algorithmType.averageCase} | Space: ${algorithmType.spaceComplexity}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        // Step index badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.testTag("engine_step_badge")
        ) {
          val totalDisplay = if (totalSteps > 0) totalSteps else 1
          Text(
            text = "Step ${currentStepIndex + 1} / $totalDisplay",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Step Action Banner / Explanation
      if (showExplanation) {
        Spacer(modifier = Modifier.height(10.dp))
        VisualizationActionBanner(
          step = currentStep,
          actionType = currentStep?.actionType ?: StepActionType.START
        )
      }

      // Live Metrics HUD (Comparisons, Swaps, Inversions)
      if (showMetrics) {
        Spacer(modifier = Modifier.height(8.dp))
        VisualizationMetricsHUD(
          step = currentStep,
          arraySize = arrayState.size
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Main Array Bars Rendering Canvas
      VisualizationBarsView(
        array = arrayState,
        step = currentStep,
        showPointers = showPointers,
        height = barHeight
      )

      // Animation Controller Controls
      if (showControls) {
        Spacer(modifier = Modifier.height(12.dp))
        VisualizationAnimationControllerControls(
          isPlaying = isPlaying,
          currentStepIndex = currentStepIndex,
          totalSteps = totalSteps,
          playbackSpeedMs = playbackSpeedMs,
          isLooping = controller.isLooping,
          onPlay = { controller.play() },
          onPause = { controller.pause() },
          onStepForward = { controller.stepForward() },
          onStepBackward = { controller.stepBackward() },
          onReset = { controller.reset() },
          onJumpToEnd = { controller.jumpToEnd() },
          onSeekTo = { controller.seekTo(it) },
          onSetSpeed = { controller.setPlaybackSpeed(it) },
          onToggleLoop = { controller.setLoop(!controller.isLooping) }
        )
      }
    }
  }
}

/**
 * Overload for existing screen architectures that manage step state externally.
 */
@Composable
fun VisualizationEngine(
  currentStep: SortStep?,
  arrayFallback: List<Int>,
  totalSteps: Int,
  currentStepIndex: Int,
  isPlaying: Boolean,
  playbackSpeedMs: Long,
  onPlay: () -> Unit,
  onPause: () -> Unit,
  onStepForward: () -> Unit,
  onStepBackward: () -> Unit,
  onReset: () -> Unit,
  onJumpToEnd: () -> Unit,
  onSeekTo: (Int) -> Unit,
  onSetSpeed: (Long) -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  algorithmType: AlgorithmType? = null,
  showControls: Boolean = true,
  showMetrics: Boolean = true,
  showExplanation: Boolean = true,
  showPointers: Boolean = true,
  barHeight: Dp = 190.dp
) {
  val arrayState = currentStep?.arrayState ?: arrayFallback

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("visualization_engine"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = title ?: algorithmType?.displayName ?: "Visualization Engine",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (algorithmType != null) {
            Text(
              text = "Time: ${algorithmType.averageCase} | Space: ${algorithmType.spaceComplexity}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.testTag("engine_step_badge")
        ) {
          val totalDisplay = if (totalSteps > 0) totalSteps else 1
          Text(
            text = "Step ${currentStepIndex + 1} / $totalDisplay",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      if (showExplanation) {
        Spacer(modifier = Modifier.height(10.dp))
        VisualizationActionBanner(
          step = currentStep,
          actionType = currentStep?.actionType ?: StepActionType.START
        )
      }

      if (showMetrics) {
        Spacer(modifier = Modifier.height(8.dp))
        VisualizationMetricsHUD(
          step = currentStep,
          arraySize = arrayState.size
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      VisualizationBarsView(
        array = arrayState,
        step = currentStep,
        showPointers = showPointers,
        height = barHeight
      )

      if (showControls) {
        Spacer(modifier = Modifier.height(12.dp))
        VisualizationAnimationControllerControls(
          isPlaying = isPlaying,
          currentStepIndex = currentStepIndex,
          totalSteps = totalSteps,
          playbackSpeedMs = playbackSpeedMs,
          isLooping = false,
          onPlay = onPlay,
          onPause = onPause,
          onStepForward = onStepForward,
          onStepBackward = onStepBackward,
          onReset = onReset,
          onJumpToEnd = onJumpToEnd,
          onSeekTo = onSeekTo,
          onSetSpeed = onSetSpeed,
          onToggleLoop = {}
        )
      }
    }
  }
}

/**
 * Action Banner summarizing the current sort step action (Comparing, Swapping, Sorted, etc.)
 */
@Composable
private fun VisualizationActionBanner(
  step: SortStep?,
  actionType: StepActionType
) {
  val (bgColor, icon, label) = when (actionType) {
    StepActionType.COMPARING -> Triple(
      Color(0xFFFEF3C7),
      Icons.AutoMirrored.Filled.ArrowForward,
      "Comparing Elements"
    )
    StepActionType.SWAPPING -> Triple(
      Color(0xFFFEE2E2),
      Icons.Default.SwapHoriz,
      "Swapping Elements"
    )
    StepActionType.MIN_FOUND -> Triple(
      Color(0xFFEDE9FE),
      Icons.Default.CheckCircle,
      "New Minimum Found"
    )
    StepActionType.ELEMENT_SORTED -> Triple(
      Color(0xFFD1FAE5),
      Icons.Default.CheckCircle,
      "Element Placed in Final Position"
    )
    StepActionType.EARLY_EXIT -> Triple(
      Color(0xFFE0E7FF),
      Icons.Default.FastForward,
      "Early Exit (Zero Swaps Detected)"
    )
    StepActionType.COMPLETE -> Triple(
      Color(0xFFD1FAE5),
      Icons.Default.CheckCircle,
      "Array Fully Sorted!"
    )
    StepActionType.START -> Triple(
      MaterialTheme.colorScheme.surfaceVariant,
      Icons.Default.BarChart,
      "Ready to Execute"
    )
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color(0xFF1E293B),
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF1E293B)
        )
        Text(
          text = step?.explanation ?: "Use the playback controls below to run the algorithm step by step.",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF334155),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * Metric indicators for Comparisons, Swaps, and Inversions.
 */
@Composable
private fun VisualizationMetricsHUD(
  step: SortStep?,
  arraySize: Int
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MetricItem(
      label = "Comparisons",
      value = "${step?.comparisonsCount ?: 0}",
      color = Color(0xFF3B82F6),
      modifier = Modifier.weight(1f)
    )
    MetricItem(
      label = "Swaps",
      value = "${step?.swapsCount ?: 0}",
      color = Color(0xFFF59E0B),
      modifier = Modifier.weight(1f)
    )
    MetricItem(
      label = "Inversions",
      value = "${step?.remainingInversions ?: 0}",
      color = Color(0xFF8B5CF6),
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun MetricItem(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        ),
        color = color
      )
    }
  }
}

/**
 * Visual array rendering area with smooth height animations, custom coloring, index badges, and pointer arrows.
 */
@Composable
private fun VisualizationBarsView(
  array: List<Int>,
  step: SortStep?,
  showPointers: Boolean,
  height: Dp
) {
  val maxValue = (array.maxOrNull() ?: 100).coerceAtLeast(10)
  val scrollState = rememberScrollState()

  val primaryIdx = step?.primaryIndex ?: -1
  val secondaryIdx = step?.secondaryIndex ?: -1
  val minIdx = step?.minCandidateIndex ?: -1
  val sortedSet = step?.sortedIndices ?: emptySet()
  val actionType = step?.actionType ?: StepActionType.START

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(height)
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .horizontalScroll(scrollState),
      horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.Bottom
    ) {
      array.forEachIndexed { index, value ->
        val isPrimary = index == primaryIdx
        val isSecondary = index == secondaryIdx
        val isMinCandidate = index == minIdx
        val isSorted = sortedSet.contains(index)

        val isSwapping = actionType == StepActionType.SWAPPING && (isPrimary || isSecondary)
        val isComparing = actionType == StepActionType.COMPARING && (isPrimary || isSecondary)

        // Color selection
        val (barBrush, borderColor) = when {
          isSwapping -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626))),
            Color(0xFFB91C1C)
          )
          isComparing -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))),
            Color(0xFFD97706)
          )
          isMinCandidate -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFC084FC), Color(0xFF9333EA))),
            Color(0xFF7E22CE)
          )
          isSorted -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF059669))),
            Color(0xFF047857)
          )
          else -> Pair(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.primary
              )
            ),
            Color.Transparent
          )
        }

        // Animate height dynamically
        val normalizedHeightFraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0.12f, 1f)
        val targetHeight = (height.value * 0.65f * normalizedHeightFraction).dp
        val animatedHeight by animateDpAsState(
          targetValue = targetHeight,
          animationSpec = spring(stiffness = 500f),
          label = "bar_height_$index"
        )

        val barWidth = if (array.size <= 8) 36.dp else if (array.size <= 14) 28.dp else 22.dp

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
          modifier = Modifier.fillMaxHeight()
        ) {
          // Pointer tags (i, j, min)
          if (showPointers) {
            Box(
              modifier = Modifier
                .height(18.dp)
                .width(barWidth),
              contentAlignment = Alignment.Center
            ) {
              when {
                isComparing && isPrimary -> Text("i", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                isComparing && isSecondary -> Text("j", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                isSwapping -> Text("↔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                isMinCandidate -> Text("min", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE))
                isSorted -> Text("✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
              }
            }
          }

          // Value above bar
          Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = if (array.size > 12) 10.sp else 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Bar Rectangle
          Box(
            modifier = Modifier
              .width(barWidth)
              .height(animatedHeight)
              .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
              .background(barBrush)
              .then(
                if (borderColor != Color.Transparent) {
                  Modifier.border(2.dp, borderColor, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                } else Modifier
              )
              .testTag("bar_item_$index")
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Index badge [0], [1], ...
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 2.dp)
          ) {
            Text(
              text = "$index",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Animation Controller panel with Scrubber slider, Play/Pause, Step controls, and Speed selectors.
 */
@Composable
fun VisualizationAnimationControllerControls(
  isPlaying: Boolean,
  currentStepIndex: Int,
  totalSteps: Int,
  playbackSpeedMs: Long,
  isLooping: Boolean,
  onPlay: () -> Unit,
  onPause: () -> Unit,
  onStepForward: () -> Unit,
  onStepBackward: () -> Unit,
  onReset: () -> Unit,
  onJumpToEnd: () -> Unit,
  onSeekTo: (Int) -> Unit,
  onSetSpeed: (Long) -> Unit,
  onToggleLoop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val maxSteps = (totalSteps - 1).coerceAtLeast(0)

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      // Timeline scrubber
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Step Timeline",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "${currentStepIndex + 1} / ${(totalSteps).coerceAtLeast(1)}",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }

      Slider(
        value = currentStepIndex.toFloat(),
        onValueChange = { onSeekTo(it.toInt()) },
        valueRange = 0f..maxSteps.toFloat(),
        steps = if (maxSteps in 1..99) maxSteps - 1 else 0,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary,
          inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(32.dp)
          .testTag("engine_timeline_slider")
      )

      // Main transport playback buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reset to beginning
        IconButton(
          onClick = onReset,
          enabled = currentStepIndex > 0,
          modifier = Modifier.testTag("engine_reset_button")
        ) {
          Icon(
            imageVector = Icons.Default.Replay,
            contentDescription = "Restart from Beginning",
            tint = if (currentStepIndex > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
          )
        }

        // Step Backward 1 step
        IconButton(
          onClick = onStepBackward,
          enabled = currentStepIndex > 0,
          modifier = Modifier.testTag("engine_step_backward_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Step Backward",
            tint = if (currentStepIndex > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
          )
        }

        // Big Play/Pause
        FilledIconButton(
          onClick = { if (isPlaying) onPause() else onPlay() },
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier
            .size(52.dp)
            .testTag("engine_play_pause_button")
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(28.dp)
          )
        }

        // Step Forward 1 step
        IconButton(
          onClick = onStepForward,
          enabled = currentStepIndex < totalSteps - 1,
          modifier = Modifier.testTag("engine_step_forward_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Step Forward",
            tint = if (currentStepIndex < totalSteps - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
          )
        }

        // Jump to End
        IconButton(
          onClick = onJumpToEnd,
          enabled = currentStepIndex < totalSteps - 1,
          modifier = Modifier.testTag("engine_jump_end_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastForward,
            contentDescription = "Jump to End",
            tint = if (currentStepIndex < totalSteps - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Speed selection chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Speed:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf(
            Triple("0.5x", 800L, "engine_speed_05x"),
            Triple("1x", 400L, "engine_speed_1x"),
            Triple("2x", 200L, "engine_speed_2x"),
            Triple("4x", 80L, "engine_speed_4x")
          ).forEach { (label, speedMs, testTag) ->
            val isSelected = playbackSpeedMs == speedMs
            FilterChip(
              selected = isSelected,
              onClick = { onSetSpeed(speedMs) },
              label = {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier
                .height(28.dp)
                .testTag(testTag)
            )
          }
        }
      }
    }
  }
}
