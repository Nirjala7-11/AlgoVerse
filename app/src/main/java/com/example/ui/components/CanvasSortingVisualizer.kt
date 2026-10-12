package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Enhanced Canvas-based Sorting Visualizer.
 * Visually demonstrates how elements physically move and interact during:
 * 1. SWAP: Elements lift, glide past each other on distinct elevation arcs to their destination.
 * 2. COMPARE: Elements physically pulse, lean toward each other, and display an overhead inequality bridge.
 */
@Composable
fun CanvasSortingVisualizer(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  arrayFallback: List<Int>,
  modifier: Modifier = Modifier
) {
  val array = currentStep?.arrayState ?: arrayFallback
  val primaryIdx = currentStep?.primaryIndex ?: -1
  val secondaryIdx = currentStep?.secondaryIndex ?: -1
  val minIdx = currentStep?.minCandidateIndex ?: -1
  val sortedIndices = currentStep?.sortedIndices ?: emptySet()
  val actionType = currentStep?.actionType ?: StepActionType.START

  val maxValue = (array.maxOrNull() ?: 100).coerceAtLeast(10)
  val scope = rememberCoroutineScope()

  // Physical swap translation animation (0f -> 1f) with generous duration so user clearly sees them move
  val swapAnim = remember { Animatable(0f) }
  LaunchedEffect(currentStep?.stepIndex, actionType) {
    if (actionType == StepActionType.SWAPPING) {
      swapAnim.snapTo(0f)
      swapAnim.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
      )
    } else {
      swapAnim.snapTo(0f)
    }
  }

  // Continuous comparison oscillation (subtle magnetic lean-in during comparison)
  val comparePulse = remember { Animatable(0f) }
  LaunchedEffect(actionType, primaryIdx, secondaryIdx) {
    if (actionType == StepActionType.COMPARING) {
      comparePulse.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 900, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    } else {
      comparePulse.snapTo(0f)
    }
  }

  // Glow pulse for highlight borders
  val glowAnim = remember { Animatable(1f) }
  LaunchedEffect(actionType, primaryIdx, secondaryIdx) {
    if (actionType == StepActionType.COMPARING || actionType == StepActionType.SWAPPING) {
      glowAnim.animateTo(
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
          animation = tween(400, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      glowAnim.snapTo(1f)
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("canvas_sorting_visualizer"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Dynamic Visual Status Header
      if (actionType == StepActionType.SWAPPING && primaryIdx >= 0 && secondaryIdx >= 0 && primaryIdx < array.size && secondaryIdx < array.size) {
        val valA = array[secondaryIdx] // Originally at primary, now in secondary
        val valB = array[primaryIdx]   // Originally at secondary, now in primary
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFEF4444).copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "ANIMATING SWAP: arr[$primaryIdx] ⇄ arr[$secondaryIdx]",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFEF4444)
                )
                Text(
                  text = "Values $valA and $valB are physically gliding across indices!",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Replay animation button
            FilledTonalButton(
              onClick = {
                scope.launch {
                  swapAnim.snapTo(0f)
                  swapAnim.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
                }
              },
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "Replay Move", modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Replay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      } else if (actionType == StepActionType.COMPARING && primaryIdx >= 0 && secondaryIdx >= 0 && primaryIdx < array.size && secondaryIdx < array.size) {
        val valA = array[primaryIdx]
        val valB = array[secondaryIdx]
        val isGreater = valA > valB
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isGreater) Color(0xFFF59E0B).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGreater) Color(0xFFF59E0B) else Color(0xFF10B981)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CompareArrows,
              contentDescription = null,
              tint = if (isGreater) Color(0xFFF59E0B) else Color(0xFF10B981),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "COMPARING: arr[$primaryIdx] = $valA  vs  arr[$secondaryIdx] = $valB",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isGreater) Color(0xFFD97706) else Color(0xFF059669)
              )
              Text(
                text = if (isGreater) "Inversion detected ($valA > $valB)! Elements must be swapped." else "Correct order ($valA ≤ $valB). No swap needed.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      } else {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = currentStep?.explanation ?: "Ready to sort array.",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            textAlign = TextAlign.Center
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Interactive Canvas Rendering with Physical Movement & Arcs
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(250.dp)
          .background(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            shape = RoundedCornerShape(12.dp)
          )
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
        ) {
          val canvasWidth = size.width
          val canvasHeight = size.height

          val bottomReserved = 48.dp.toPx()
          val topReserved = 42.dp.toPx()
          val chartHeight = canvasHeight - bottomReserved - topReserved
          val baselineY = canvasHeight - bottomReserved

          // Grid lines
          val gridLineCount = 4
          for (g in 0..gridLineCount) {
            val y = topReserved + (chartHeight / gridLineCount) * g
            drawLine(
              color = Color.Gray.copy(alpha = 0.15f),
              start = Offset(0f, y),
              end = Offset(canvasWidth, y),
              strokeWidth = 1.dp.toPx()
            )
          }

          val n = array.size
          if (n == 0) return@Canvas

          val spacing = if (n > 20) 2.dp.toPx() else if (n > 12) 4.dp.toPx() else 8.dp.toPx()
          val totalSpacing = spacing * (n + 1)
          val availableWidth = canvasWidth - totalSpacing
          val barWidth = (availableWidth / n).coerceAtLeast(4.dp.toPx())

          // Calculate default X coordinates
          val defaultXs = FloatArray(n) { i ->
            spacing + i * (barWidth + spacing)
          }

          // Compute animated positions for bars
          val renderedXs = FloatArray(n) { i -> defaultXs[i] }
          val elevationY = FloatArray(n) { 0f }

          // Physical Movement during SWAP
          if (actionType == StepActionType.SWAPPING && primaryIdx in 0 until n && secondaryIdx in 0 until n) {
            val progress = swapAnim.value
            val pOrigX = defaultXs[primaryIdx]
            val sOrigX = defaultXs[secondaryIdx]

            // In SWAPPING step, array[primaryIdx] has the new value that came from secondaryIdx,
            // and array[secondaryIdx] has the new value that came from primaryIdx.
            // Therefore, array[primaryIdx] travels from sOrigX to pOrigX,
            // and array[secondaryIdx] travels from pOrigX to sOrigX!
            renderedXs[primaryIdx] = sOrigX + (pOrigX - sOrigX) * progress
            renderedXs[secondaryIdx] = pOrigX + (sOrigX - pOrigX) * progress

            // High arch for primary traveling element, lower dip/arch for secondary element
            elevationY[primaryIdx] = -sin(progress * PI).toFloat() * 36.dp.toPx()
            elevationY[secondaryIdx] = -sin(progress * PI).toFloat() * 16.dp.toPx()
          }

          // Subtle physical lean-in during COMPARISON (shows attraction/focus)
          if (actionType == StepActionType.COMPARING && primaryIdx in 0 until n && secondaryIdx in 0 until n) {
            val leanDistance = 3.dp.toPx() * sin(comparePulse.value * PI * 2).toFloat()
            renderedXs[primaryIdx] = defaultXs[primaryIdx] + leanDistance
            renderedXs[secondaryIdx] = defaultXs[secondaryIdx] - leanDistance
          }

          // Draw bars
          for (i in 0 until n) {
            val value = array[i]
            val heightRatio = (value.toFloat() / maxValue).coerceIn(0.08f, 1f)
            val barHeight = chartHeight * heightRatio
            val left = renderedXs[i]
            val top = baselineY - barHeight + elevationY[i]
            val centerX = left + barWidth / 2f

            val isPrimary = i == primaryIdx
            val isSecondary = i == secondaryIdx
            val isMin = i == minIdx
            val isSorted = sortedIndices.contains(i)

            val (fillColorTop, fillColorBottom) = when {
              isSorted -> Pair(Color(0xFF34D399), Color(0xFF059669)) // Sorted Emerald
              (isPrimary || isSecondary) && actionType == StepActionType.SWAPPING ->
                Pair(Color(0xFFF87171), Color(0xFFDC2626)) // Swapping Red
              (isPrimary || isSecondary) && actionType == StepActionType.COMPARING ->
                Pair(Color(0xFFFBBF24), Color(0xFFD97706)) // Comparing Amber
              isMin && algorithm == AlgorithmType.SELECTION_SORT ->
                Pair(Color(0xFFA78BFA), Color(0xFF7C3AED)) // Selection Min Violet
              else -> Pair(Color(0xFF6366F1), Color(0xFF4338CA)) // Default Indigo
            }

            val barCorner = (barWidth / 3f).coerceAtMost(6.dp.toPx())

            // Shadow/Glow underneath lifted bars during swap
            if (elevationY[i] < -2.dp.toPx()) {
              drawRoundRect(
                color = Color.Black.copy(alpha = 0.25f),
                topLeft = Offset(left, baselineY - 4.dp.toPx()),
                size = Size(barWidth, 4.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
              )
            }

            drawRoundRect(
              brush = Brush.verticalGradient(
                colors = listOf(fillColorTop, fillColorBottom),
                startY = top,
                endY = baselineY + elevationY[i]
              ),
              topLeft = Offset(left, top),
              size = Size(barWidth, barHeight),
              cornerRadius = CornerRadius(barCorner, barCorner)
            )

            // Dynamic highlight border
            if (isPrimary || isSecondary || isMin) {
              val strokeColor = if (actionType == StepActionType.SWAPPING) Color.White else Color(0xFFFEF08A)
              drawRoundRect(
                color = strokeColor,
                topLeft = Offset(left - 1.dp.toPx(), top - 1.dp.toPx()),
                size = Size(barWidth + 2.dp.toPx(), barHeight + 2.dp.toPx()),
                cornerRadius = CornerRadius(barCorner, barCorner),
                style = Stroke(
                  width = 2.dp.toPx() * (if (actionType == StepActionType.SWAPPING) glowAnim.value else 1f)
                )
              )
            }

            // Numeric Value label on top of bar
            val textPaint = android.graphics.Paint().apply {
              color = if (isPrimary || isSecondary || isMin) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#94A3B8")
              textSize = if (n > 18) 22f else 28f
              isFakeBoldText = isPrimary || isSecondary || isMin
              textAlign = android.graphics.Paint.Align.CENTER
              isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
              "$value",
              centerX,
              top - 6.dp.toPx(),
              textPaint
            )

            // Index label below baseline
            val indexPaint = android.graphics.Paint().apply {
              color = android.graphics.Color.parseColor("#64748B")
              textSize = if (n > 18) 18f else 22f
              textAlign = android.graphics.Paint.Align.CENTER
              isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
              "[$i]",
              centerX,
              baselineY + 16.dp.toPx(),
              indexPaint
            )

            // Pointer tags: i, j, j+1, min
            val tagText = when {
              isPrimary && isSecondary -> "i,j"
              isPrimary && algorithm == AlgorithmType.BUBBLE_SORT -> "j"
              isSecondary && algorithm == AlgorithmType.BUBBLE_SORT -> "j+1"
              isPrimary && algorithm == AlgorithmType.SELECTION_SORT -> "i"
              isSecondary && algorithm == AlgorithmType.SELECTION_SORT -> "j"
              isMin && algorithm == AlgorithmType.SELECTION_SORT -> "min"
              else -> null
            }

            if (tagText != null) {
              val tagColor = when {
                actionType == StepActionType.SWAPPING -> android.graphics.Color.parseColor("#EF4444")
                actionType == StepActionType.COMPARING -> android.graphics.Color.parseColor("#F59E0B")
                isMin -> android.graphics.Color.parseColor("#A78BFA")
                else -> android.graphics.Color.parseColor("#6366F1")
              }
              val tagPaint = android.graphics.Paint().apply {
                color = tagColor
                textSize = 20f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
              }
              drawContext.canvas.nativeCanvas.drawText(
                tagText,
                centerX,
                baselineY + 34.dp.toPx(),
                tagPaint
              )
            }
          }

          // Overhead physical trajectory arc & comparison bridges
          if (primaryIdx in 0 until n && secondaryIdx in 0 until n && primaryIdx != secondaryIdx) {
            val x1 = renderedXs[primaryIdx] + barWidth / 2f
            val x2 = renderedXs[secondaryIdx] + barWidth / 2f
            val arcMidX = (x1 + x2) / 2f
            val arcTopY = topReserved - 14.dp.toPx()

            val arcPath = Path().apply {
              moveTo(x1, topReserved)
              quadraticTo(arcMidX, arcTopY, x2, topReserved)
            }

            if (actionType == StepActionType.SWAPPING) {
              // Glowing flight path trail for swap
              drawPath(
                path = arcPath,
                color = Color(0xFFEF4444).copy(alpha = 0.8f),
                style = Stroke(
                  width = 3.dp.toPx(),
                  cap = StrokeCap.Round
                )
              )

              // Swap crossover indicator node in mid-air
              drawCircle(
                color = Color(0xFFEF4444),
                radius = 6.dp.toPx(),
                center = Offset(arcMidX, arcTopY + 4.dp.toPx())
              )
              drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(arcMidX, arcTopY + 4.dp.toPx())
              )
            } else if (actionType == StepActionType.COMPARING) {
              // Dashed comparison bridge
              drawPath(
                path = arcPath,
                color = Color(0xFFF59E0B),
                style = Stroke(
                  width = 2.dp.toPx(),
                  cap = StrokeCap.Round,
                  pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), comparePulse.value * 20f)
                )
              )

              // Comparison operator in mid-air
              val valA = array[primaryIdx]
              val valB = array[secondaryIdx]
              val opSymbol = if (valA > valB) ">" else "≤"
              val opPaint = android.graphics.Paint().apply {
                color = if (valA > valB) android.graphics.Color.parseColor("#F59E0B") else android.graphics.Color.parseColor("#10B981")
                textSize = 28f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
              }
              drawContext.canvas.nativeCanvas.drawText(
                opSymbol,
                arcMidX,
                arcTopY + 2.dp.toPx(),
                opPaint
              )
            }
          }
        }
      }

      // Legend Row with high-contrast labels
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        LegendDot(color = Color(0xFFF59E0B), label = "Comparing")
        LegendDot(color = Color(0xFFEF4444), label = "Swapping")
        LegendDot(color = Color(0xFF10B981), label = "Sorted")
        if (algorithm == AlgorithmType.SELECTION_SORT) {
          LegendDot(color = Color(0xFF8B5CF6), label = "Min Found")
        }
        LegendDot(color = Color(0xFF6366F1), label = "Unsorted")
      }
    }
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(9.dp)
        .background(color, RoundedCornerShape(2.dp))
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
