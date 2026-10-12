package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ArrayPreset

@Composable
fun PresetsRow(
  onSelectPreset: (ArrayPreset) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    PresetButton(
      label = "Already sorted",
      icon = Icons.Default.NorthEast,
      testTag = "preset_already_sorted",
      onClick = { onSelectPreset(ArrayPreset.ALREADY_SORTED) }
    )
    PresetButton(
      label = "Reverse sorted",
      icon = Icons.Default.SouthEast,
      testTag = "preset_reverse_sorted",
      onClick = { onSelectPreset(ArrayPreset.REVERSE_SORTED) }
    )
    PresetButton(
      label = "All duplicates",
      icon = Icons.Default.FormatListNumbered,
      testTag = "preset_all_duplicates",
      onClick = { onSelectPreset(ArrayPreset.ALL_DUPLICATES) }
    )
    PresetButton(
      label = "Single element",
      icon = Icons.Default.Filter1,
      testTag = "preset_single_element",
      onClick = { onSelectPreset(ArrayPreset.SINGLE_ELEMENT) }
    )
    PresetButton(
      label = "Random",
      icon = Icons.Default.Casino,
      testTag = "preset_random",
      onClick = { onSelectPreset(ArrayPreset.RANDOM) }
    )
  }
}

@Composable
private fun PresetButton(
  label: String,
  icon: ImageVector,
  testTag: String,
  onClick: () -> Unit
) {
  FilterChip(
    selected = false,
    onClick = onClick,
    label = {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium
      )
    },
    leadingIcon = {
      Icon(
        imageVector = icon,
        contentDescription = label,
        modifier = Modifier.size(16.dp)
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
      labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    modifier = Modifier
      .heightIn(min = 48.dp)
      .testTag(testTag)
  )
}
