package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType

@Composable
fun ArrayBarVisualizer(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  arrayFallback: List<Int>,
  modifier: Modifier = Modifier
) {
  val array = currentStep?.arrayState ?: arrayFallback
  val primaryIdx = currentStep?.primaryIndex ?: -1
  val secondaryIdx = currentStep?.secondaryIndex ?: -1
  val minIdx = currentStep?.minCandidateIndex ?: -1
  val sortedSet = currentStep?.sortedIndices ?: emptySet()
  val actionType = currentStep?.actionType ?: StepActionType.START

  val maxValue = (array.maxOrNull() ?: 100).coerceAtLeast(10)
  val scrollState = rememberScrollState()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("array_bar_visualizer"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
    ) {
      // Step Explanation Text
      Text(
        text = currentStep?.explanation ?: "Ready to sort array.",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Main Bars Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
          .horizontalScroll(scrollState),
        contentAlignment = Alignment.BottomCenter
      ) {
        Row(
          modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(if (array.size > 16) 4.dp else 8.dp),
          verticalAlignment = Alignment.Bottom
        ) {
          array.forEachIndexed { index, value ->
            val isPrimary = index == primaryIdx
            val isSecondary = index == secondaryIdx
            val isMin = index == minIdx
            val isSorted = sortedSet.contains(index)

            // Determine bar color state
            val (targetColor, targetGradientEnd) = when {
              isSorted -> Pair(Color(0xFF10B981), Color(0xFF059669)) // Sorted Green
              (isPrimary || isSecondary) && actionType == StepActionType.SWAPPING ->
                Pair(Color(0xFFEF4444), Color(0xFFDC2626)) // Swapping Red
              (isPrimary || isSecondary) && actionType == StepActionType.COMPARING ->
                Pair(Color(0xFFF59E0B), Color(0xFFD97706)) // Comparing Amber
              isMin && algorithm == AlgorithmType.SELECTION_SORT ->
                Pair(Color(0xFF8B5CF6), Color(0xFF7C3AED)) // Selection Min Violet
              else -> Pair(Color(0xFF3B82F6), Color(0xFF2563EB)) // Default Blue
            }

            val animatedColor by animateColorAsState(targetValue = targetColor, label = "barColor_$index")
            val animatedEndColor by animateColorAsState(targetValue = targetGradientEnd, label = "barEndColor_$index")

            // Proportional Height
            val heightRatio = (value.toFloat() / maxValue).coerceIn(0.12f, 1f)
            val animatedHeightFraction by animateFloatAsState(
              targetValue = heightRatio,
              animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
              label = "heightFraction_$index"
            )

            // Bar Column (Value label, Bar itself, Index label, Active Pointer badge)
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier.fillMaxHeight()
            ) {
              // Value Label
              Text(
                text = "$value",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = if (array.size > 18) 10.sp else 12.sp,
                  fontWeight = if (isPrimary || isSecondary || isMin) FontWeight.Bold else FontWeight.Normal,
                  fontFamily = FontFamily.Monospace
                ),
                color = if (isPrimary || isSecondary || isMin) animatedColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 2.dp)
              )

              // The animated Bar
              Box(
                modifier = Modifier
                  .height((135 * animatedHeightFraction).dp)
                  .width(if (array.size > 20) 18.dp else if (array.size > 12) 26.dp else 36.dp)
                  .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(animatedColor, animatedEndColor)
                    )
                  )
                  .then(
                    if (isPrimary || isSecondary || isMin) {
                      Modifier.border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                      )
                    } else Modifier
                  )
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Index Label
              Text(
                text = "[$index]",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = if (array.size > 18) 9.sp else 11.sp,
                  fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.outline
              )

              // Pointer Label badge
              val pointerText = when {
                isPrimary && isSecondary -> "i,j"
                isPrimary && algorithm == AlgorithmType.BUBBLE_SORT -> "j"
                isSecondary && algorithm == AlgorithmType.BUBBLE_SORT -> "j+1"
                isPrimary && algorithm == AlgorithmType.SELECTION_SORT -> "i"
                isSecondary && algorithm == AlgorithmType.SELECTION_SORT -> "j"
                isMin && algorithm == AlgorithmType.SELECTION_SORT -> "min"
                else -> ""
              }

              Box(
                modifier = Modifier
                  .height(18.dp)
                  .padding(top = 2.dp),
                contentAlignment = Alignment.Center
              ) {
                if (pointerText.isNotEmpty()) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = animatedColor.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = pointerText,
                      color = animatedColor,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Legend row
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        LegendItem(color = Color(0xFFF59E0B), label = "Comparing")
        LegendItem(color = Color(0xFFEF4444), label = "Swapping")
        LegendItem(color = Color(0xFF10B981), label = "Sorted")
        if (algorithm == AlgorithmType.SELECTION_SORT) {
          LegendItem(color = Color(0xFF8B5CF6), label = "Min Candidate")
        }
      }
    }
  }
}

@Composable
private fun LegendItem(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .width(10.dp)
        .height(10.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
