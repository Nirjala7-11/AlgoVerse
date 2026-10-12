package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.SortStep
import com.example.model.StepActionType
import com.example.viewmodel.PredictFeedback

/**
 * PredictModeWidget: An interactive quiz-style overlay for the VisualizationEngine
 * that asks the user to predict the next step in the algorithm before allowing
 * them to continue playback.
 */
@Composable
fun PredictModeWidget(
  currentStep: SortStep?,
  isPredictModeActive: Boolean,
  isStepAnswered: Boolean,
  feedback: PredictFeedback?,
  currentStreak: Int,
  accuracyPercentage: Int,
  onSubmitPrediction: (Boolean) -> Unit,
  onContinuePlayback: () -> Unit,
  modifier: Modifier = Modifier,
  onDismissOrPeek: (() -> Unit)? = null
) {
  // Only render when predict mode is active and current step requires a student prediction
  val shouldShow = isPredictModeActive && currentStep != null && currentStep.requiresPrediction

  AnimatedVisibility(
    visible = shouldShow,
    enter = fadeIn(tween(250)) + scaleIn(spring(stiffness = 600f), initialScale = 0.92f),
    exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.92f),
    modifier = modifier.testTag("predict_mode_widget")
  ) {
    if (currentStep == null) return@AnimatedVisibility

    val val1 = if (currentStep.primaryIndex in currentStep.arrayState.indices) {
      currentStep.arrayState[currentStep.primaryIndex]
    } else null

    val val2 = if (currentStep.secondaryIndex in currentStep.arrayState.indices) {
      currentStep.arrayState[currentStep.secondaryIndex]
    } else null

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(12.dp, RoundedCornerShape(20.dp)),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      border = androidx.compose.foundation.BorderStroke(
        width = 2.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFF6366F1), Color(0xFF38BDF8), Color(0xFFF59E0B))
        )
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header: Quiz Badge & Streak HUD
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Psychology,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "PREDICT STEP",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                  ),
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF59E0B).copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Whatshot,
                  contentDescription = null,
                  tint = Color(0xFFF59E0B),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "$currentStreak Streak",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFF59E0B)
                )
              }
            }
          }

          if (accuracyPercentage > 0) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF10B981).copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.EmojiEvents,
                  contentDescription = null,
                  tint = Color(0xFF10B981),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "$accuracyPercentage% Accuracy",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF10B981)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question Banner & Elements Comparison Spotlight
        if (val1 != null && val2 != null) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 14.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Element 1 Box
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "arr[${currentStep.primaryIndex}]",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF3B82F6),
                  modifier = Modifier.size(width = 54.dp, height = 36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "$val1",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    )
                  }
                }
              }

              // Comparison Icon / Vs badge
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.SwapHoriz,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(24.dp)
                )
                Text(
                  text = if (val1 > val2) "$val1 > $val2" else "$val1 ≤ $val2",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              // Element 2 Box
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "arr[${currentStep.secondaryIndex}]",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF8B5CF6),
                  modifier = Modifier.size(width = 54.dp, height = 36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "$val2",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        // Quiz Question Text
        Text(
          text = currentStep.predictPrompt.ifEmpty { "Will these elements swap in this step?" },
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quiz Interactive Area: Options vs Feedback
        if (feedback != null) {
          // Feedback view with Explanation and Continue Button
          val isCorrect = feedback.isCorrect
          val feedbackColor = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = feedbackColor.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, feedbackColor),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(feedbackColor),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                  Text(
                    text = if (isCorrect) "Spot On! Correct Prediction 🎉" else "Not Quite! Review the logic:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = feedbackColor
                  )
                  Text(
                    text = if (isCorrect) "+10 points added to your mastery!" else "Keep practicing to build your intuition!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = feedback.explanation.ifEmpty {
                  if (currentStep.predictWillSwapOrUpdate) {
                    "Since arr[${currentStep.primaryIndex}] > arr[${currentStep.secondaryIndex}], the algorithm must swap them to preserve order."
                  } else {
                    "Since arr[${currentStep.primaryIndex}] <= arr[${currentStep.secondaryIndex}], they are already in relative order; no swap is needed."
                  }
                },
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Continue Playback Button
              Button(
                onClick = onContinuePlayback,
                colors = ButtonDefaults.buttonColors(containerColor = feedbackColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("predict_continue_button")
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Continue Playback",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        } else {
          // Two Choice Buttons (Yes, Swap vs No, Keep)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { onSubmitPrediction(true) },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("predict_option_yes")
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = currentStep.predictOptionYesText.ifEmpty { "Yes, Swap!" },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }

            FilledTonalButton(
              onClick = { onSubmitPrediction(false) },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("predict_option_no")
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = currentStep.predictOptionNoText.ifEmpty { "No, Keep" },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Playback paused: choose an option to reveal the result and proceed.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

/**
 * PredictOverlayContainer: A wrapper that displays the VisualizationEngine with
 * the PredictModeWidget rendered as a floating overlay directly over the array canvas.
 */
@Composable
fun PredictOverlayContainer(
  isPredictModeActive: Boolean,
  currentStep: SortStep?,
  isStepAnswered: Boolean,
  feedback: PredictFeedback?,
  currentStreak: Int,
  accuracyPercentage: Int,
  onSubmitPrediction: (Boolean) -> Unit,
  onContinuePlayback: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Box(modifier = modifier.fillMaxWidth()) {
    content()

    if (isPredictModeActive && currentStep != null && currentStep.requiresPrediction) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        PredictModeWidget(
          currentStep = currentStep,
          isPredictModeActive = isPredictModeActive,
          isStepAnswered = isStepAnswered,
          feedback = feedback,
          currentStreak = currentStreak,
          accuracyPercentage = accuracyPercentage,
          onSubmitPrediction = onSubmitPrediction,
          onContinuePlayback = onContinuePlayback
        )
      }
    }
  }
}
