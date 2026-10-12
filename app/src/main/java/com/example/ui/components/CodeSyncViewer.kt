package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.SortStep
import com.example.model.StepActionType

data class PseudocodeLine(
  val lineNumber: Int,
  val code: String,
  val explanation: String,
  val complexityNote: String
)

/**
 * Interactive Pseudocode Panel with Real-Time State Synchronization.
 * Features:
 * - Line-by-line step synchronization highlighting the active line corresponding to the visualization state.
 * - Glowing gutter arrow with animated transition.
 * - Interactive line selection: tap any line to inspect its algorithmic purpose and complexity.
 * - Live Scope Watch Deck: shows real-time variables (i, j, minIdx, arr[j], evaluations) at the current step.
 * - Step forward / backward execution controls directly integrated inside the panel.
 */
@Composable
fun CodeSyncViewer(
  algorithm: AlgorithmType,
  currentStep: SortStep?,
  onStepForward: () -> Unit = {},
  onStepBackward: () -> Unit = {},
  onReset: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showScopeInspector by remember { mutableStateOf(true) }
  var userSelectedLineNumber by remember { mutableStateOf<Int?>(null) }
  var languageMode by remember { mutableStateOf(0) } // 0 = Standard Pseudocode, 1 = Kotlin / Java

  // Resolve active line based on current step action and line index
  val activeLine = remember(algorithm, currentStep?.stepIndex, currentStep?.actionType) {
    resolveActiveLine(algorithm, currentStep)
  }

  val pseudocodeLines = remember(algorithm, languageMode) {
    getPseudocodeFor(algorithm, languageMode)
  }

  // Pulsing animation for active line indicator
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("interactive_pseudocode_panel"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFF0B1120) // Deep modern IDE editor background
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = Color(0xFF1E293B)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // IDE Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Terminal/Editor Window Dots
          Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
            Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
            Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
          }

          Spacer(modifier = Modifier.width(10.dp))

          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(16.dp)
          )

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = "${algorithm.displayName} • Step Trace",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFF1F5F9)
          )
        }

        // Language toggle (Pseudocode vs Kotlin)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          FilterChip(
            selected = languageMode == 0,
            onClick = { languageMode = 0 },
            label = { Text("Pseudocode", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF1E293B),
              selectedLabelColor = Color(0xFF38BDF8),
              containerColor = Color.Transparent,
              labelColor = Color(0xFF64748B)
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = languageMode == 0,
              borderColor = Color(0xFF334155),
              selectedBorderColor = Color(0xFF38BDF8)
            ),
            modifier = Modifier.height(26.dp)
          )

          FilterChip(
            selected = languageMode == 1,
            onClick = { languageMode = 1 },
            label = { Text("Kotlin", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF1E293B),
              selectedLabelColor = Color(0xFF38BDF8),
              containerColor = Color.Transparent,
              labelColor = Color(0xFF64748B)
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = languageMode == 1,
              borderColor = Color(0xFF334155),
              selectedBorderColor = Color(0xFF38BDF8)
            ),
            modifier = Modifier.height(26.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Execution Status Gutter Strip
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF131D31),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(Color(0xFF10B981), CircleShape)
                .alpha(pulseAlpha)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Executing Line $activeLine: ${currentStep?.actionType?.name ?: "START"}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              ),
              color = Color(0xFF38BDF8)
            )
          }

          Text(
            text = "Step ${(currentStep?.stepIndex ?: 0) + 1} / ${currentStep?.totalSteps ?: 1}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp
            ),
            color = Color(0xFF94A3B8)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Interactive Pseudocode Line Deck
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
          .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
          .padding(vertical = 6.dp)
      ) {
        pseudocodeLines.forEach { lineItem ->
          val isActive = lineItem.lineNumber == activeLine
          val isUserInspecting = userSelectedLineNumber == lineItem.lineNumber

          val rowBg by animateColorAsState(
            targetValue = when {
              isActive -> Color(0xFF1E3A8A).copy(alpha = 0.45f) // Deep active blue
              isUserInspecting -> Color(0xFF334155).copy(alpha = 0.35f)
              else -> Color.Transparent
            },
            label = "line_bg_${lineItem.lineNumber}"
          )

          val borderModifier = if (isActive) {
            Modifier.border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
          } else if (isUserInspecting) {
            Modifier.border(1.dp, Color(0xFF94A3B8).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
          } else {
            Modifier
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 6.dp, vertical = 2.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(rowBg)
              .then(borderModifier)
              .clickable {
                userSelectedLineNumber = if (userSelectedLineNumber == lineItem.lineNumber) null else lineItem.lineNumber
              }
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag("pseudocode_line_${lineItem.lineNumber}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Gutter: Active Indicator + Line Number
            Row(
              modifier = Modifier.width(36.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isActive) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = "Active line",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier
                    .size(12.dp)
                    .alpha(pulseAlpha)
                )
              } else {
                Spacer(modifier = Modifier.width(12.dp))
              }

              Text(
                text = "${lineItem.lineNumber}",
                color = if (isActive) Color(0xFF38BDF8) else Color(0xFF64748B),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(start = 2.dp)
              )
            }

            // Syntax Highlighted Code
            Text(
              text = formatSyntaxHighlight(lineItem.code),
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.weight(1f)
            )

            // Active badge or info tag
            if (isActive) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
              ) {
                Text(
                  text = "NOW",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                  ),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            } else if (isUserInspecting) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      // Line Inspection Card (shown when user taps a line)
      val inspectedLine = pseudocodeLines.find { it.lineNumber == (userSelectedLineNumber ?: activeLine) }
      if (inspectedLine != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF131D31),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("line_inspection_card")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = null,
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Line ${inspectedLine.lineNumber} Explanation ${if (inspectedLine.lineNumber == activeLine) "(Active)" else "(Inspected)"}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9)
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF1E293B)
              ) {
                Text(
                  text = inspectedLine.complexityNote,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = inspectedLine.explanation,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Collapsible Live Variable Watch Deck Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { showScopeInspector = !showScopeInspector }
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Live Scope Watch (Variables & Conditions)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFF1F5F9)
          )
        }

        Icon(
          imageVector = if (showScopeInspector) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(18.dp)
        )
      }

      // Live Scope Watch Deck Content
      AnimatedVisibility(
        visible = showScopeInspector,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
            .padding(10.dp)
            .testTag("live_scope_watch_deck")
        ) {
          val array = currentStep?.arrayState ?: emptyList()
          val pIdx = currentStep?.primaryIndex ?: -1
          val sIdx = currentStep?.secondaryIndex ?: -1
          val mIdx = currentStep?.minCandidateIndex ?: -1

          when (algorithm) {
            AlgorithmType.BUBBLE_SORT -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ScopeVarTile(label = "j (idx)", value = if (pIdx >= 0) "$pIdx" else "–", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "j + 1", value = if (sIdx >= 0) "$sIdx" else "–", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "arr[j]", value = if (pIdx in array.indices) "${array[pIdx]}" else "–", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "arr[j+1]", value = if (sIdx in array.indices) "${array[sIdx]}" else "–", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Condition Evaluation
              val valA = if (pIdx in array.indices) array[pIdx] else null
              val valB = if (sIdx in array.indices) array[sIdx] else null
              val condEvaluated = if (valA != null && valB != null) {
                if (valA > valB) "Condition (arr[j] > arr[j+1] ⇒ $valA > $valB): TRUE ➔ SWAP" else "Condition (arr[j] > arr[j+1] ⇒ $valA ≤ $valB): FALSE ➔ KEEP"
              } else {
                "Evaluating pass status..."
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (valA != null && valB != null && valA > valB) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (valA != null && valB != null && valA > valB) Color(0xFFEF4444) else Color(0xFF10B981)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = condEvaluated,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                  ),
                  color = if (valA != null && valB != null && valA > valB) Color(0xFFF87171) else Color(0xFF34D399),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            AlgorithmType.SELECTION_SORT -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ScopeVarTile(label = "i (target)", value = if (pIdx >= 0) "$pIdx" else "–", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "minIdx", value = if (mIdx >= 0) "$mIdx" else "–", color = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "j (scan)", value = if (sIdx >= 0) "$sIdx" else "–", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "arr[minIdx]", value = if (mIdx in array.indices) "${array[mIdx]}" else "–", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
              }

              Spacer(modifier = Modifier.height(8.dp))

              val valJ = if (sIdx in array.indices) array[sIdx] else null
              val valMin = if (mIdx in array.indices) array[mIdx] else null
              val condEvaluated = if (valJ != null && valMin != null) {
                if (valJ < valMin) "Condition (arr[j] < arr[minIdx] ⇒ $valJ < $valMin): TRUE ➔ NEW MIN" else "Condition (arr[j] < arr[minIdx] ⇒ $valJ ≥ $valMin): FALSE ➔ HOLD"
              } else {
                "Selection partition locked"
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (valJ != null && valMin != null && valJ < valMin) Color(0xFFA78BFA).copy(alpha = 0.15f) else Color(0xFF0284C7).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (valJ != null && valMin != null && valJ < valMin) Color(0xFFA78BFA) else Color(0xFF0284C7)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = condEvaluated,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                  ),
                  color = if (valJ != null && valMin != null && valJ < valMin) Color(0xFFC084FC) else Color(0xFF38BDF8),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            else -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ScopeVarTile(label = "Primary (idx)", value = if (pIdx >= 0) "$pIdx" else "–", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "Secondary", value = if (sIdx >= 0) "$sIdx" else "–", color = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "arr[primary]", value = if (pIdx in array.indices) "${array[pIdx]}" else "–", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                ScopeVarTile(label = "Action", value = currentStep?.actionType?.name ?: "IDLE", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Secondary metrics row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Total Comparisons: ${currentStep?.comparisonsCount ?: 0}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = Color(0xFF94A3B8)
            )
            Text(
              text = "Total Swaps: ${currentStep?.swapsCount ?: 0}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = Color(0xFF94A3B8)
            )
            Text(
              text = "Inversions Left: ${currentStep?.remainingInversions ?: 0}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Direct Step Execution Toolbar inside the panel
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onStepBackward,
          modifier = Modifier
            .size(36.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Step Back", tint = Color(0xFFF1F5F9), modifier = Modifier.size(16.dp))
        }

        Button(
          onClick = onStepForward,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0284C7),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("step_into_code_button")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Step Into Line (Execute)",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        IconButton(
          onClick = onReset,
          modifier = Modifier
            .size(36.dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color(0xFFF1F5F9), modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
private fun ScopeVarTile(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = Color(0xFF1E293B),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = Color(0xFF94A3B8)
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp
        ),
        color = color
      )
    }
  }
}

/**
 * Resolves which line number in the pseudocode corresponds to the current visualization step.
 */
private fun resolveActiveLine(algorithm: AlgorithmType, step: SortStep?): Int {
  if (step == null) return 1
  return when (algorithm) {
    AlgorithmType.BUBBLE_SORT -> when (step.actionType) {
      StepActionType.START -> 1
      StepActionType.COMPARING -> 4
      StepActionType.SWAPPING -> 5
      StepActionType.ELEMENT_SORTED -> 2
      StepActionType.EARLY_EXIT -> 6
      StepActionType.COMPLETE -> 7
      else -> step.codeLineNumber.coerceIn(1, 7)
    }

    AlgorithmType.SELECTION_SORT -> when (step.actionType) {
      StepActionType.START -> 1
      StepActionType.MIN_FOUND -> 3
      StepActionType.COMPARING -> 5
      StepActionType.SWAPPING -> 8
      StepActionType.ELEMENT_SORTED -> 2
      StepActionType.COMPLETE -> 9
      else -> step.codeLineNumber.coerceIn(1, 9)
    }

    else -> step.codeLineNumber.coerceAtLeast(1)
  }
}

private fun getPseudocodeFor(algorithm: AlgorithmType, languageMode: Int): List<PseudocodeLine> {
  return when (algorithm) {
    AlgorithmType.BUBBLE_SORT -> if (languageMode == 0) {
      listOf(
        PseudocodeLine(1, "function bubbleSort(arr, n):", "Initializes the bubble sort routine on an array of size n.", "Cost: O(1)"),
        PseudocodeLine(2, "  for i from 0 to n - 2 do:", "Outer loop runs (n - 1) passes to bubble successive largest elements.", "Iterates: n - 1 times"),
        PseudocodeLine(3, "    for j from 0 to n - i - 2 do:", "Inner loop traverses unsorted adjacent pairs.", "Comparisons: n(n-1)/2 total"),
        PseudocodeLine(4, "      if arr[j] > arr[j + 1] then:", "Relational check: detects if an inversion exists between adjacent items.", "Check: 1 comparison"),
        PseudocodeLine(5, "        swap(arr[j], arr[j + 1])", "Exchanges adjacent items, resolving exactly 1 array inversion.", "Writes: 3 assignments"),
        PseudocodeLine(6, "    if no swaps in pass: break", "Adaptive check: if 0 swaps occurred, array is already in order!", "Best Case: O(n) exit"),
        PseudocodeLine(7, "  return arr", "Halts and returns the verified non-decreasing sorted array.", "Total: O(n²) worst")
      )
    } else {
      listOf(
        PseudocodeLine(1, "fun bubbleSort(arr: IntArray) {", "Function declaration with in-place integer array parameter.", "Memory: In-place"),
        PseudocodeLine(2, "  for (i in 0 until arr.size - 1) {", "Outer pass loop index tracking completed sorted partitions.", "Loop: 0..(n-2)"),
        PseudocodeLine(3, "    for (j in 0 until arr.size - i - 1) {", "Inner adjacent comparison window.", "Bounds: 0..(n-i-2)"),
        PseudocodeLine(4, "      if (arr[j] > arr[j + 1]) {", "Condition evaluating whether left item exceeds right item.", "Comparison"),
        PseudocodeLine(5, "        arr[j] = arr[j + 1].also { arr[j + 1] = arr[j] }", "Idiomatic Kotlin in-place swap using standard function.", "Swap"),
        PseudocodeLine(6, "    if (!swapped) break", "Adaptive early break flag optimization.", "Flag optimization"),
        PseudocodeLine(7, "}", "End of sorting procedure.", "Returns Unit")
      )
    }

    AlgorithmType.SELECTION_SORT -> if (languageMode == 0) {
      listOf(
        PseudocodeLine(1, "function selectionSort(arr, n):", "Initializes Selection Sort routine on array of size n.", "Cost: O(1)"),
        PseudocodeLine(2, "  for i from 0 to n - 2 do:", "Outer loop iterates each position i that must be filled with the true minimum.", "Iterates: n - 1 times"),
        PseudocodeLine(3, "    minIdx = i", "Tentatively assumes index i holds the minimum value of unsorted suffix.", "Assignment: O(1)"),
        PseudocodeLine(4, "    for j from i + 1 to n - 1 do:", "Inner loop scans remainder of array to find the true minimum.", "Comparisons: n(n-1)/2"),
        PseudocodeLine(5, "      if arr[j] < arr[minIdx] then:", "Compares current scan candidate with active minimum.", "Comparison check"),
        PseudocodeLine(6, "        minIdx = j", "Updates minimum index pointer to the newly discovered smaller item.", "Pointer update"),
        PseudocodeLine(7, "    if minIdx != i then:", "Checks whether a swap is necessary to place the minimum element.", "Branch check"),
        PseudocodeLine(8, "      swap(arr[i], arr[minIdx])", "Places minimum item into its permanent position i. At most 1 swap per pass!", "Max Writes: n - 1"),
        PseudocodeLine(9, "  return arr", "Returns array with all n elements in sorted non-decreasing order.", "Total: Θ(n²)")
      )
    } else {
      listOf(
        PseudocodeLine(1, "fun selectionSort(arr: IntArray) {", "Function declaration with in-place integer array.", "Memory: In-place"),
        PseudocodeLine(2, "  for (i in 0 until arr.size - 1) {", "Iterates destination target index i.", "Loop: 0..(n-2)"),
        PseudocodeLine(3, "    var minIdx = i", "Local minimum candidate tracking index.", "Local var"),
        PseudocodeLine(4, "    for (j in i + 1 until arr.size) {", "Unsorted suffix scanner loop.", "Bounds: (i+1)..(n-1)"),
        PseudocodeLine(5, "      if (arr[j] < arr[minIdx]) minIdx = j", "Updates minIdx if smaller element found.", "Comparison & update"),
        PseudocodeLine(6, "    if (minIdx != i) {", "Guards swap execution.", "Condition"),
        PseudocodeLine(7, "      val temp = arr[i]", "Temporary buffer for swap.", "Assignment"),
        PseudocodeLine(8, "      arr[i] = arr[minIdx]; arr[minIdx] = temp", "Executes exchange of target and minimum elements.", "Write operation"),
        PseudocodeLine(9, "  }", "End of selection sort procedure.", "Returns Unit")
      )
    }

    AlgorithmType.INSERTION_SORT -> listOf(
      PseudocodeLine(1, "function insertionSort(arr, n):", "Initializes incremental insertion on prefix.", "O(1) Aux"),
      PseudocodeLine(2, "  for i from 1 to n - 1 do:", "Picks element arr[i] to insert into sorted prefix.", "n - 1 passes"),
      PseudocodeLine(3, "    key = arr[i]; j = i - 1", "Buffers key value and points to end of sorted prefix.", "O(1) space"),
      PseudocodeLine(4, "    while j >= 0 and arr[j] > key:", "Scans backward, shifting larger elements right.", "Adaptive"),
      PseudocodeLine(5, "      arr[j + 1] = arr[j]; j = j - 1", "Shift larger element to open slot.", "1 write"),
      PseudocodeLine(6, "    arr[j + 1] = key", "Inserts key into its correct sorted location.", "1 write")
    )

    AlgorithmType.MERGE_SORT -> listOf(
      PseudocodeLine(1, "function mergeSort(arr, low, high):", "Divide & conquer recursive formulation.", "T(n) = 2T(n/2) + O(n)"),
      PseudocodeLine(2, "  if low < high then:", "Base case check for single element subarray.", "Base check"),
      PseudocodeLine(3, "    mid = (low + high) / 2", "Divides problem into two equal halves.", "O(1) divide"),
      PseudocodeLine(4, "    mergeSort(arr, low, mid)", "Recursively sorts left half.", "T(n/2)"),
      PseudocodeLine(5, "    mergeSort(arr, mid + 1, high)", "Recursively sorts right half.", "T(n/2)"),
      PseudocodeLine(6, "    merge(arr, low, mid, high)", "Linear merge combining sorted halves.", "O(n) merge")
    )

    AlgorithmType.QUICK_SORT -> listOf(
      PseudocodeLine(1, "function quickSort(arr, low, high):", "Divide & conquer in-place partition sorting.", "O(log n) stack"),
      PseudocodeLine(2, "  if low < high then:", "Base case check for partition boundaries.", "Base check"),
      PseudocodeLine(3, "    pivot = arr[high]", "Chooses pivot element for Lomuto partitioning.", "Pivot selection"),
      PseudocodeLine(4, "    p = partition(arr, low, high)", "Rearranges so items < pivot are left, items >= pivot are right.", "O(n) scan"),
      PseudocodeLine(5, "    quickSort(arr, low, p - 1)", "Recursively sorts left sub-partition.", "Left branch"),
      PseudocodeLine(6, "    quickSort(arr, p + 1, high)", "Recursively sorts right sub-partition.", "Right branch")
    )

    AlgorithmType.HEAP_SORT -> listOf(
      PseudocodeLine(1, "function heapSort(arr, n):", "In-place comparison sort using binary max-heap.", "O(1) Aux space"),
      PseudocodeLine(2, "  buildMaxHeap(arr, n)", "Builds initial max-heap bottom-up in O(n) time.", "O(n) build"),
      PseudocodeLine(3, "  for i from n - 1 downTo 1 do:", "Successively extracts maximum element from root.", "n - 1 extractions"),
      PseudocodeLine(4, "    swap(arr[0], arr[i])", "Moves current maximum root to its finalized sorted position.", "1 swap"),
      PseudocodeLine(5, "    heapify(arr, i, 0)", "Restores max-heap property on remaining i elements.", "O(log n) sift-down"),
      PseudocodeLine(6, "  return arr", "Array is sorted in non-decreasing order.", "Total: O(n log n)")
    )
  }
}

/**
 * Rich syntax highlighting for pseudocode tokens.
 */
private fun formatSyntaxHighlight(codeText: String) = buildAnnotatedString {
  val tokens = codeText.split(" ")
  tokens.forEachIndexed { idx, token ->
    when {
      // Keywords
      token in listOf("function", "fun", "for", "from", "to", "until", "in", "do", "do:", "if", "then", "then:", "return", "break", "var", "val") -> {
        withStyle(SpanStyle(color = Color(0xFFC084FC), fontWeight = FontWeight.Bold)) {
          append(token)
        }
      }
      // Functions
      token.startsWith("swap") || token.startsWith("bubbleSort") || token.startsWith("selectionSort") -> {
        withStyle(SpanStyle(color = Color(0xFF34D399), fontWeight = FontWeight.Bold)) {
          append(token)
        }
      }
      // Operators & Numbers
      token in listOf(">", "<", "!=", "=", "==", "+", "-", "//", "0", "1", "2") || token.all { it.isDigit() } -> {
        withStyle(SpanStyle(color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)) {
          append(token)
        }
      }
      // Comments
      token.startsWith("//") -> {
        withStyle(SpanStyle(color = Color(0xFF64748B), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
          append(token)
        }
      }
      // Variables & Identifiers
      token.contains("arr") || token.contains("minIdx") || token.contains("swapped") -> {
        withStyle(SpanStyle(color = Color(0xFF38BDF8))) {
          append(token)
        }
      }
      else -> {
        withStyle(SpanStyle(color = Color(0xFFE2E8F0))) {
          append(token)
        }
      }
    }
    if (idx < tokens.size - 1) append(" ")
  }
}
