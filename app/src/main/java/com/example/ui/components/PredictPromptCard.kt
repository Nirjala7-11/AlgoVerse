package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.SortStep
import com.example.viewmodel.PredictFeedback

@Composable
fun PredictPromptCard(
  predictModeEnabled: Boolean,
  onTogglePredictMode: () -> Unit,
  currentStep: SortStep?,
  feedback: PredictFeedback?,
  totalPredictions: Int,
  correctPredictions: Int,
  currentStreak: Int,
  accuracyPercentage: Int,
  isStepAnswered: Boolean,
  onSubmitPrediction: (Boolean) -> Unit,
  onContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("predict_prompt_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (predictModeEnabled) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
      }
    ),
    border = if (predictModeEnabled) {
      androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
    } else null
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Mode Title, Switch, and Accuracy stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = if (predictModeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Predict-Before-Reveal",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (predictModeEnabled) "Active • Tests your step logic" else "Enable to test your comprehension",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        Switch(
          checked = predictModeEnabled,
          onCheckedChange = { onTogglePredictMode() },
          modifier = Modifier.testTag("predict_mode_switch"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
          )
        )
      }

      // Stats Chip Row (Streak & Accuracy)
      if (totalPredictions > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Accuracy: $accuracyPercentage% ($correctPredictions/$totalPredictions)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          if (currentStreak > 0) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF59E0B).copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Whatshot,
                  contentDescription = null,
                  tint = Color(0xFFF59E0B),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$currentStreak Streak!",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFF59E0B)
                )
              }
            }
          }
        }
      }

      // Active Prediction Interactive Prompt
      AnimatedVisibility(
        visible = predictModeEnabled && currentStep?.requiresPrediction == true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        ) {
          if (feedback != null) {
            // Feedback Card
            val isCorrect = feedback.isCorrect
            val feedbackColor = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = feedbackColor.copy(alpha = 0.12f),
              border = androidx.compose.foundation.BorderStroke(1.dp, feedbackColor.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = feedbackColor,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (isCorrect) "Spot on! Correct prediction 🎉" else "Not quite!",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = feedbackColor
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = feedback.explanation,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                  onClick = onContinue,
                  colors = ButtonDefaults.buttonColors(containerColor = feedbackColor),
                  modifier = Modifier
                    .align(Alignment.End)
                    .testTag("continue_step_button")
                ) {
                  Text("Continue Step")
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
              }
            }
          } else if (!isStepAnswered) {
            // Prediction Question & Options
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Student Prediction Checkpoint",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = currentStep?.predictPrompt ?: "What will happen next in this comparison?",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = { onSubmitPrediction(true) },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("predict_yes_button"),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primary
                    )
                  ) {
                    Text(
                      text = currentStep?.predictOptionYesText ?: "Yes",
                      style = MaterialTheme.typography.labelMedium
                    )
                  }

                  FilledTonalButton(
                    onClick = { onSubmitPrediction(false) },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("predict_no_button")
                  ) {
                    Text(
                      text = currentStep?.predictOptionNoText ?: "No",
                      style = MaterialTheme.typography.labelMedium
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
