package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlinx.coroutines.launch

/**
 * Interactive Compare & Swap Inspector.
 * Clearly visualizes how elements are compared and how they physically slide & swap positions.
 */
@Composable
fun SwapCompareInspector(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  arrayFallback: List<Int>,
  onExecuteStep: () -> Unit,
  modifier: Modifier = Modifier
) {
  val step = currentStep ?: return
  val array = step.arrayState.ifEmpty { arrayFallback }
  val primaryIdx = step.primaryIndex
  val secondaryIdx = step.secondaryIndex

  if (primaryIdx < 0 || secondaryIdx < 0 || primaryIdx >= array.size || secondaryIdx >= array.size) {
    return
  }

  val valA = array[primaryIdx]
  val valB = array[secondaryIdx]
  val isComparing = step.actionType == StepActionType.COMPARING
  val isSwapping = step.actionType == StepActionType.SWAPPING
  val willSwap = step.predictWillSwapOrUpdate || (isComparing && valA > valB)

  val scope = rememberCoroutineScope()
  val buttonScaleAnim = remember { Animatable(1f) }

  // Physical slide animation between the two element cards during swap
  val slideAnim = remember { Animatable(0f) }
  LaunchedEffect(step.stepIndex, isSwapping) {
    if (isSwapping) {
      slideAnim.snapTo(0f)
      slideAnim.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
      )
    } else {
      slideAnim.snapTo(0f)
    }
  }

  // Accent Colors
  val accentColor = if (isSwapping) Color(0xFFEF4444) else if (valA > valB) Color(0xFFF59E0B) else Color(0xFF10B981)
  val containerBg = accentColor.copy(alpha = 0.10f)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("swap_compare_inspector")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header Label
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = accentColor,
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (isSwapping) Icons.Default.SwapHoriz else Icons.Default.CompareArrows,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (isSwapping) "Step: Swapping Elements" else "Step: Comparing Elements",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (isSwapping) "Physical exchange of inverted values" else "Relational check: arr[i] > arr[j]",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = accentColor.copy(alpha = 0.18f),
          border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)
        ) {
          Text(
            text = if (isSwapping) "SWAP ⇄" else if (valA > valB) "INVERTED !" else "IN ORDER ✓",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = accentColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Animated Side-by-Side Element Comparison Tiles (Physical Slide during Swap)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val slideOffsetPx = (slideAnim.value * 40).dp.value

        // Element A Card
        Box(modifier = Modifier.offset { IntOffset(slideOffsetPx.toInt(), 0) }) {
          ElementInspectionTile(
            label = if (algorithm == AlgorithmType.BUBBLE_SORT) "arr[$primaryIdx] (Left)" else "arr[$primaryIdx] (i)",
            value = valA,
            highlightColor = if (isSwapping) Color(0xFFEF4444) else Color(0xFFF59E0B)
          )
        }

        // Comparison Operator / Swap Arrows in Center
        Column(
          modifier = Modifier.padding(horizontal = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp,
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              if (isSwapping) {
                Icon(
                  imageVector = Icons.Default.SwapHoriz,
                  contentDescription = "Swapping",
                  tint = Color(0xFFEF4444),
                  modifier = Modifier.size(24.dp)
                )
              } else {
                Text(
                  text = if (valA > valB) ">" else "≤",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = if (valA > valB) Color(0xFFF59E0B) else Color(0xFF10B981)
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = if (isSwapping) "Swapping" else if (valA > valB) "Swap!" else "Hold",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              color = accentColor
            )
          )
        }

        // Element B Card
        Box(modifier = Modifier.offset { IntOffset(-slideOffsetPx.toInt(), 0) }) {
          ElementInspectionTile(
            label = if (algorithm == AlgorithmType.BUBBLE_SORT) "arr[$secondaryIdx] (Right)" else "arr[$secondaryIdx] (j)",
            value = valB,
            highlightColor = if (isSwapping) Color(0xFFEF4444) else Color(0xFF38BDF8)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Educational Intuition & Inversion Explanation Box
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = if (isSwapping) {
              "Exchange executed: Values $valA and $valB have swapped positions, resolving an inversion!"
            } else if (valA > valB) {
              "Inversion Detected: Since arr[$primaryIdx] = $valA > arr[$secondaryIdx] = $valB, these elements are out of order and must be swapped!"
            } else {
              "Correct Order: Since arr[$primaryIdx] = $valA ≤ arr[$secondaryIdx] = $valB, no swap is required. Continuing to next element."
            },
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface
            )
          )

          if (step.remainingInversions > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${step.remainingInversions} inversions left in array",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Right Bottom Corner Button inside Inspector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Button(
          onClick = {
            scope.launch {
              buttonScaleAnim.snapTo(0.92f)
              buttonScaleAnim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))
            }
            onExecuteStep()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .scale(buttonScaleAnim.value)
            .testTag("execute_swap_button")
        ) {
          Icon(
            imageVector = if (isSwapping) {
              Icons.Default.CheckCircle
            } else if (valA > valB) {
              Icons.Default.SwapHoriz
            } else {
              Icons.AutoMirrored.Filled.ArrowForward
            },
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = when {
              isSwapping -> "Confirm Swap & Continue ➔"
              valA > valB -> "Execute Swap ⇄"
              else -> "Next Comparison ➔"
            },
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun ElementInspectionTile(
  label: String,
  value: Int,
  highlightColor: Color
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, highlightColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.width(110.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "$value",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace
        ),
        color = highlightColor
      )
    }
  }
}
