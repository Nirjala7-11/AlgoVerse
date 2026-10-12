package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ExamQuestion(
  val id: Int,
  val unit: String,
  val title: String,
  val prompt: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val solutionExplanation: String,
  val relevantTopic: String
)

@Composable
fun ExamBankScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit = {},
  onNavigateTopic: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  val questions = listOf(
    ExamQuestion(
      id = 1,
      unit = "Unit 1: Sorting",
      title = "Bubble Sort Pass 1 Inversions",
      prompt = "Given array [5, 1, 4, 2, 8], how many adjacent swaps are executed during the FIRST pass of Bubble Sort?",
      options = listOf("1 swap", "2 swaps", "3 swaps", "4 swaps"),
      correctOptionIndex = 2,
      solutionExplanation = "Pass 1: (5>1 swap -> [1,5,4,2,8]), (5>4 swap -> [1,4,5,2,8]), (5>2 swap -> [1,4,2,5,8]), (5<8 no swap). Total: 3 swaps! Verify in Topic 1.",
      relevantTopic = "Topic 1: Compare/Swap"
    ),
    ExamQuestion(
      id = 2,
      unit = "Unit 2: Divide & Conquer",
      title = "Merge Sort Depth & Work",
      prompt = "For an array of size n = 16, what is the maximum tree depth of recursive calls in Merge Sort, and what is the work done at EACH level?",
      options = listOf("Depth 4, O(n) work per level", "Depth 16, O(1) work per level", "Depth 8, O(log n) work per level", "Depth 2, O(n²) work per level"),
      correctOptionIndex = 0,
      solutionExplanation = "log2(16) = 4 levels of recursion. Each level performs exactly c·n merge operations, giving total O(n log n). Verify in Topic 2.",
      relevantTopic = "Topic 2: Recursion Tree"
    ),
    ExamQuestion(
      id = 3,
      unit = "Unit 3: Dynamic Programming",
      title = "0/1 Knapsack Dependency",
      prompt = "In 0/1 Knapsack, to compute cell dp[i][w] when item i is included, which previous subproblem cell is referenced?",
      options = listOf("dp[i-1][w]", "dp[i-1][w - weight[i]]", "dp[i][w - 1]", "dp[i+1][w]"),
      correctOptionIndex = 1,
      solutionExplanation = "If item i is included, remaining capacity is w - weight[i], which comes from the previous item row: dp[i-1][w - weight[i]]. Verify in Topic 3.",
      relevantTopic = "Topic 3: DP Table"
    ),
    ExamQuestion(
      id = 4,
      unit = "Unit 4: Greedy Algorithms",
      title = "Greedy vs 0/1 Knapsack",
      prompt = "Why does the Greedy highest-value-per-weight density heuristic fail on 0/1 Knapsack?",
      options = listOf("Sorting takes O(n log n) which is too slow", "0/1 Knapsack requires taking whole items; greedy choice may leave empty space that wastes capacity", "Values cannot be floats", "Greedy always works for knapsack"),
      correctOptionIndex = 1,
      solutionExplanation = "Greedy heuristic is indivisible in 0/1: taking a high-ratio item might prevent taking two items that yield higher total value. Verify in Topic 4.",
      relevantTopic = "Topic 4: Greedy vs DP"
    )
  )

  val userAnswers = remember { mutableStateMapOf<Int, Int>() }
  val revealedSolutions = remember { mutableStateMapOf<Int, Boolean>() }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(14.dp)
  ) {
    // Header
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackToDashboard) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Dashboard",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          Icon(
            imageVector = Icons.Default.Quiz,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Topic 8: ADA Hand-Trace Exam Bank",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "University Exam Patterns & Visualizer Verifications",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Button(
          onClick = onOpenSidebar,
          modifier = Modifier.testTag("exam_sidebar_button")
        ) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    questions.forEach { q ->
      val selectedOption = userAnswers[q.id]
      val isRevealed = revealedSolutions[q.id] == true

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Unit Tag
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = q.unit,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
            Text(
              text = "Q#${q.id}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = q.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = q.prompt,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Options
          q.options.forEachIndexed { optIdx, optText ->
            val isChosen = selectedOption == optIdx
            val isCorrect = optIdx == q.correctOptionIndex

            val optBg = when {
              isRevealed && isCorrect -> Color(0xFF10B981).copy(alpha = 0.18f)
              isRevealed && isChosen && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.18f)
              isChosen -> MaterialTheme.colorScheme.primaryContainer
              else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }

            val optBorderColor = when {
              isRevealed && isCorrect -> Color(0xFF10B981)
              isRevealed && isChosen && !isCorrect -> Color(0xFFEF4444)
              isChosen -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = optBg,
              border = androidx.compose.foundation.BorderStroke(1.dp, optBorderColor),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Button(
                  onClick = { userAnswers[q.id] = optIdx },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isChosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.size(32.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                  Text(
                    text = "${'A' + optIdx}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = optText,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Action Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledTonalButton(
              onClick = { revealedSolutions[q.id] = !(revealedSolutions[q.id] ?: false) }
            ) {
              Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isRevealed) "Hide Explanation" else "Verify Answer",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (isRevealed) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF10B981).copy(alpha = 0.4f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "Solution & Visualizer Verification:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                  )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = q.solutionExplanation,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}
