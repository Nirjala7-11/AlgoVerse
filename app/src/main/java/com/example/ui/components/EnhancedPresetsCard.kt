package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ArraySortEngine
import com.example.model.ArrayPreset

data class PresetUiModel(
  val preset: ArrayPreset,
  val title: String,
  val icon: ImageVector,
  val badgeText: String,
  val badgeColor: Color,
  val samplePreview: List<Int>,
  val description: String
)

@Composable
fun EnhancedPresetsCard(
  selectedPreset: ArrayPreset?,
  onSelectPreset: (ArrayPreset) -> Unit,
  modifier: Modifier = Modifier
) {
  val presets = listOf(
    PresetUiModel(
      preset = ArrayPreset.ALREADY_SORTED,
      title = "Already Sorted",
      icon = Icons.Default.NorthEast,
      badgeText = "Best Case • O(n)",
      badgeColor = Color(0xFF10B981),
      samplePreview = listOf(6, 14, 22, 35, 48, 60, 75, 89),
      description = "0 Inversions • Early-exit finishes in 1 pass!"
    ),
    PresetUiModel(
      preset = ArrayPreset.REVERSE_SORTED,
      title = "Reverse Sorted",
      icon = Icons.Default.SouthEast,
      badgeText = "Worst Case • O(n²)",
      badgeColor = Color(0xFFEF4444),
      samplePreview = listOf(89, 75, 60, 48, 35, 22, 14, 6),
      description = "Max 28 Inversions • Max swaps & comparisons"
    ),
    PresetUiModel(
      preset = ArrayPreset.ALL_DUPLICATES,
      title = "All Duplicates",
      icon = Icons.Default.FormatListNumbered,
      badgeText = "Uniform Data",
      badgeColor = Color(0xFF38BDF8),
      samplePreview = listOf(30, 30, 30, 30, 30, 30, 30, 30),
      description = "Tests algorithm stability • 0 swaps"
    ),
    PresetUiModel(
      preset = ArrayPreset.SINGLE_ELEMENT,
      title = "Single Element",
      icon = Icons.Default.Filter1,
      badgeText = "Trivial • O(1)",
      badgeColor = Color(0xFF8B5CF6),
      samplePreview = listOf(42),
      description = "Edge case: Array of length 1 requires 0 ops"
    ),
    PresetUiModel(
      preset = ArrayPreset.RANDOM,
      title = "Random Permutation",
      icon = Icons.Default.Casino,
      badgeText = "Average Case • O(n²)",
      badgeColor = Color(0xFFF59E0B),
      samplePreview = listOf(45, 12, 85, 32, 89, 21, 67),
      description = "Random inversions for practical benchmarking"
    )
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Select Array Benchmark Preset:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Text(
        text = "Tap to load",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      presets.forEach { item ->
        val isSelected = selectedPreset == item.preset
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF334155),
          label = "border_${item.title}"
        )

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color(0xFF1E293B)
          ),
          border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
          modifier = Modifier
            .width(220.dp)
            .clickable { onSelectPreset(item.preset) }
            .testTag("preset_card_${item.preset.name.lowercase()}")
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            // Header: Title + Icon + Check
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = item.icon,
                  contentDescription = null,
                  tint = item.badgeColor,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = item.title,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }

              if (isSelected) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Selected",
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Complexity Badge
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = item.badgeColor.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(0.5.dp, item.badgeColor.copy(alpha = 0.5f))
            ) {
              Text(
                text = item.badgeText,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = item.badgeColor
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mini Bar Chart Preview
            MiniBarChart(data = item.samplePreview, color = item.badgeColor)

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
              text = item.description,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = Color(0xFF94A3B8),
              maxLines = 2
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MiniBarChart(data: List<Int>, color: Color) {
  val maxVal = (data.maxOrNull() ?: 100).coerceAtLeast(1)
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(30.dp)
      .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.Bottom
  ) {
    data.forEach { v ->
      val fraction = (v.toFloat() / maxVal).coerceIn(0.15f, 1f)
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(fraction)
          .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
          .background(color)
      )
    }
  }
}
