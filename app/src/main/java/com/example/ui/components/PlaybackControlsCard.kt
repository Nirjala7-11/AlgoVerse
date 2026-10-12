package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackControlsCard(
  isPlaying: Boolean,
  currentStepIndex: Int,
  totalSteps: Int,
  playbackSpeedMs: Long,
  onPlay: () -> Unit,
  onPause: () -> Unit,
  onStepForward: () -> Unit,
  onStepBackward: () -> Unit,
  onReset: () -> Unit,
  onJumpToEnd: () -> Unit,
  onSeekTo: (Int) -> Unit,
  onSetSpeed: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("playback_controls_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Step Timeline Header & Scrubber Slider
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Step Timeline",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )

        val totalDisplay = if (totalSteps > 0) totalSteps else 1
        Text(
          text = "Step ${currentStepIndex + 1} / $totalDisplay",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }

      // Slider
      val maxSliderValue = if (totalSteps > 1) (totalSteps - 1).toFloat() else 1f
      Slider(
        value = currentStepIndex.toFloat().coerceIn(0f, maxSliderValue),
        onValueChange = { onSeekTo(it.toInt()) },
        valueRange = 0f..maxSliderValue,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("timeline_slider"),
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary
        )
      )

      // Primary Transport Controls Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reset / Replay
        IconButton(
          onClick = onReset,
          modifier = Modifier
            .size(48.dp)
            .testTag("reset_button")
        ) {
          Icon(
            imageVector = Icons.Default.Replay,
            contentDescription = "Restart from step 0"
          )
        }

        // Step Backward
        IconButton(
          onClick = onStepBackward,
          enabled = currentStepIndex > 0,
          modifier = Modifier
            .size(48.dp)
            .testTag("step_backward_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Step backward"
          )
        }

        // Play / Pause FAB-like icon button
        FilledIconButton(
          onClick = { if (isPlaying) onPause() else onPlay() },
          modifier = Modifier
            .size(56.dp)
            .testTag("play_pause_button"),
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier.size(28.dp)
          )
        }

        // Step Forward
        IconButton(
          onClick = onStepForward,
          enabled = currentStepIndex < totalSteps - 1,
          modifier = Modifier
            .size(48.dp)
            .testTag("step_forward_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Step forward"
          )
        }

        // Jump to End
        IconButton(
          onClick = onJumpToEnd,
          enabled = currentStepIndex < totalSteps - 1,
          modifier = Modifier
            .size(48.dp)
            .testTag("jump_to_end_button")
        ) {
          Icon(
            imageVector = Icons.Default.FastForward,
            contentDescription = "Jump to finished step"
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Speed selector row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Speed:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(end = 8.dp)
        )

        SpeedChip(label = "0.5x", delayMs = 1200L, currentSpeedMs = playbackSpeedMs, onSelect = onSetSpeed)
        Spacer(modifier = Modifier.width(6.dp))
        SpeedChip(label = "1x", delayMs = 800L, currentSpeedMs = playbackSpeedMs, onSelect = onSetSpeed)
        Spacer(modifier = Modifier.width(6.dp))
        SpeedChip(label = "2x", delayMs = 400L, currentSpeedMs = playbackSpeedMs, onSelect = onSetSpeed)
        Spacer(modifier = Modifier.width(6.dp))
        SpeedChip(label = "4x", delayMs = 150L, currentSpeedMs = playbackSpeedMs, onSelect = onSetSpeed)
      }
    }
  }
}

@Composable
private fun SpeedChip(
  label: String,
  delayMs: Long,
  currentSpeedMs: Long,
  onSelect: (Long) -> Unit
) {
  val isSelected = currentSpeedMs == delayMs
  FilterChip(
    selected = isSelected,
    onClick = { onSelect(delayMs) },
    label = {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = MaterialTheme.colorScheme.primary,
      selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    ),
    modifier = Modifier.height(32.dp)
  )
}
