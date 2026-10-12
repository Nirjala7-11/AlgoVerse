package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlgorithmType
import com.example.model.MasteryTier
import com.example.model.QuizResult
import com.example.viewmodel.AlgoVerseUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAlgoQuizSheet(
  uiState: AlgoVerseUiState,
  onDismiss: () -> Unit,
  onSelectAnswer: (questionId: Int, optionIndex: Int) -> Unit,
  onNextQuestion: () -> Unit,
  onRestartQuiz: () -> Unit,
  onNavigateToTopic: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scrollState = rememberScrollState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    modifier = modifier.testTag("post_algo_quiz_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .padding(bottom = 32.dp)
    ) {
      // Sheet Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Mastery Checkpoint",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (uiState.currentAlgorithm == AlgorithmType.BUBBLE_SORT) "Bubble Sort Comprehension" else "Selection Sort Comprehension",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_quiz_sheet_button")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (!uiState.quizCompleted && uiState.quizQuestions.isNotEmpty()) {
        val qIndex = uiState.quizCurrentIndex
        val currentQ = uiState.quizQuestions.getOrNull(qIndex)

        if (currentQ != null) {
          val selectedOption = uiState.quizSelectedAnswers[currentQ.id]
          val isAnswered = selectedOption != null

          // Progress indicator
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Question ${qIndex + 1} of ${uiState.quizQuestions.size}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = currentQ.conceptTag,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          val progress = ((qIndex + 1).toFloat() / uiState.quizQuestions.size)
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Question Card
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = currentQ.prompt,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Options
          currentQ.options.forEachIndexed { optIndex, optText ->
            val isSelected = selectedOption == optIndex
            val isCorrect = optIndex == currentQ.correctIndex

            val containerColor = when {
              isAnswered && isCorrect -> Color(0xFF10B981).copy(alpha = 0.15f)
              isAnswered && isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.15f)
              isSelected -> MaterialTheme.colorScheme.primaryContainer
              else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
              isAnswered && isCorrect -> Color(0xFF10B981)
              isAnswered && isSelected && !isCorrect -> Color(0xFFEF4444)
              isSelected -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = containerColor,
              border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(enabled = !isAnswered) {
                  onSelectAnswer(currentQ.id, optIndex)
                }
                .testTag("quiz_option_${optIndex}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = CircleShape,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.size(28.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${'A' + optIndex}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                  text = optText,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f)
                )

                if (isAnswered) {
                  if (isCorrect) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Correct",
                      tint = Color(0xFF10B981),
                      modifier = Modifier.size(20.dp)
                    )
                  } else if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Incorrect",
                      tint = Color(0xFFEF4444),
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
          }

          // Explanation Card if answered
          AnimatedVisibility(
            visible = isAnswered,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Column {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selectedOption == currentQ.correctIndex) {
                  Color(0xFF10B981).copy(alpha = 0.12f)
                } else {
                  Color(0xFFF59E0B).copy(alpha = 0.12f)
                },
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (selectedOption == currentQ.correctIndex) Color(0xFF10B981) else Color(0xFFF59E0B)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Psychology,
                      contentDescription = null,
                      tint = if (selectedOption == currentQ.correctIndex) Color(0xFF10B981) else Color(0xFFD97706),
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (selectedOption == currentQ.correctIndex) "Correct! Conceptual Takeaway:" else "Insight & Intuition:",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selectedOption == currentQ.correctIndex) Color(0xFF059669) else Color(0xFFB45309)
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = currentQ.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Next button
          Button(
            onClick = onNextQuestion,
            enabled = isAnswered,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("quiz_next_button")
          ) {
            Text(
              text = if (qIndex < uiState.quizQuestions.size - 1) "Next Question ➔" else "Complete Quiz & View Learning Path ➔",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      } else if (uiState.quizCompleted && uiState.quizResult != null) {
        // Quiz Results & Learning Path Suggestion View
        QuizResultView(
          result = uiState.quizResult,
          onNavigateTopic = { topicId ->
            onDismiss()
            onNavigateToTopic(topicId)
          },
          onRetake = onRestartQuiz
        )
      }
    }
  }
}

@Composable
private fun QuizResultView(
  result: QuizResult,
  onNavigateTopic: (String) -> Unit,
  onRetake: () -> Unit
) {
  val tierColor = when (result.tier) {
    MasteryTier.ADVANCED -> Color(0xFF10B981)
    MasteryTier.PROFICIENT -> Color(0xFF3B82F6)
    MasteryTier.FOUNDATION -> Color(0xFFF59E0B)
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    // Score Badge & Tier Header
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, tierColor),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = CircleShape,
          color = tierColor.copy(alpha = 0.15f),
          modifier = Modifier.size(64.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "${result.scorePercent}%",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = tierColor
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = result.tierTitle,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${result.correctCount} of ${result.totalQuestions} questions correct",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = result.tierDescription,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Suggested Learning Path Banner
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.Explore,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Tailored Learning Path Recommendation",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Recommended Next Step Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      ),
      border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("suggested_path_card")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary
          ) {
            Text(
              text = "NEXT RECOMMENDED STAGE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 9.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = result.suggestedTopicTitle,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = result.suggestedTopicSubtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = { onNavigateTopic(result.suggestedTopicId) },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("launch_suggested_topic_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = result.suggestedActionLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Retake Quiz button
    FilledTonalButton(
      onClick = onRetake,
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("retake_quiz_button")
    ) {
      Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Retake Checkpoint Quiz", fontWeight = FontWeight.Bold)
    }
  }
}
