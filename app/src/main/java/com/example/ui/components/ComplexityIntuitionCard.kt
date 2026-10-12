package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.AlgorithmType

@Composable
fun ComplexityIntuitionCard(
  algorithm: AlgorithmType,
  currentArrayInversions: Int,
  arraySize: Int,
  modifier: Modifier = Modifier
) {
  var simulatedN by remember { mutableFloatStateOf(arraySize.coerceIn(5, 50).toFloat()) }
  val n = simulatedN.toInt()

  val linearOps = n
  val quadraticOps = n * n
  val maxComparisons = n * (n - 1) / 2

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("complexity_intuition_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.ShowChart,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Complexity Intuition & Big-O Bounds",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Inversion theorem highlight
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Functions,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Inversion Count Theorem",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "An inversion is any pair (i, j) where i < j but arr[i] > arr[j]. In Bubble Sort, every swap resolves EXACTLY one inversion. Thus, the total swaps required to sort an array is identically equal to its initial inversion count ($currentArrayInversions for your input array)!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Interactive N Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Simulate Scale: n = $n elements",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Comparisons: $maxComparisons",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
          )
        )
      }

      Slider(
        value = simulatedN,
        onValueChange = { simulatedN = it },
        valueRange = 4f..100f,
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.testTag("simulated_n_slider")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Curve Comparison Bars
      CurveBar(label = "Best Case O(n)", ops = linearOps, maxOps = quadraticOps, color = Color(0xFF10B981))
      Spacer(modifier = Modifier.height(6.dp))
      CurveBar(label = "Comparisons n(n-1)/2", ops = maxComparisons, maxOps = quadraticOps, color = Color(0xFFF59E0B))
      Spacer(modifier = Modifier.height(6.dp))
      CurveBar(label = "Worst Case O(n²)", ops = quadraticOps, maxOps = quadraticOps, color = Color(0xFFEF4444))

      Spacer(modifier = Modifier.height(14.dp))

      // Exam Tip Card
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "🎓 University Exam Must-Know:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF38BDF8)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• Bubble Sort is ADAPTIVE with early-exit flag (Best: O(n)), STABLE (never swaps equal elements).\n• Selection Sort is NON-ADAPTIVE (always n(n-1)/2 comparisons), UNSTABLE (e.g. [4a, 4b, 1] swaps 4a with 1, placing 4a after 4b), but minimizes memory writes (at most n-1 swaps).",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Color(0xFFE2E8F0)
          )
        }
      }
    }
  }
}

@Composable
private fun CurveBar(
  label: String,
  ops: Int,
  maxOps: Int,
  color: Color
) {
  val fraction = (ops.toFloat() / maxOps.coerceAtLeast(1)).coerceIn(0.04f, 1f)
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "$ops operations",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(fraction)
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(color)
      )
    }
  }
}
