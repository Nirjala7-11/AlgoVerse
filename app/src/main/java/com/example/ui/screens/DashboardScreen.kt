package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TopicCatalog
import com.example.ui.components.AlgorithmComplexitySummaryDeck
import com.example.viewmodel.AlgoVerseViewModel

@Composable
fun DashboardScreen(
  viewModel: AlgoVerseViewModel,
  onNavigateTopic: (String) -> Unit,
  onOpenSidebar: () -> Unit,
  onOpenAiTutor: () -> Unit,
  lastTopicId: String = "bubble_sort",
  isDarkMode: Boolean = false,
  onToggleTheme: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val scrollState = rememberScrollState()

  val lastTopic = TopicCatalog.allTopics.find { it.id == lastTopicId } ?: TopicCatalog.allTopics.first()

  // Decluttering filter: All, Core, Dynamic Prog, Graphs & Adv, Theory
  var selectedCategoryFilter by remember { mutableStateOf("All") }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onOpenSidebar,
              modifier = Modifier
                .size(42.dp)
                .testTag("dashboard_hamburger_button")
            ) {
              Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open Sidebar Navigation",
                tint = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AlgoVerse",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = "ADA Hub",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                      fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Algorithms & Design Hub",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Top Action Buttons: Theme Toggle & Socratic AI Mentor
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Theme Mode Toggle Button
            IconButton(
              onClick = onToggleTheme,
              modifier = Modifier
                .size(40.dp)
                .testTag("dashboard_theme_toggle")
            ) {
              Icon(
                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // AI Mentor Button
            FilledTonalButton(
              onClick = onOpenAiTutor,
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("open_ai_mentor_button")
            ) {
              Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "AI Mentor",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(WindowInsets.navigationBars.asPaddingValues())
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      val completionPercentage = if (uiState.steps.isNotEmpty()) {
        (((uiState.currentStepIndex + 1).toFloat() / uiState.steps.size.toFloat()) * 100).toInt().coerceIn(0, 100)
      } else {
        0
      }

      // 1. Unified Clean "Active Focus" Card (Decluttered Resume + Key Metrics + % Completion)
      ActiveFocusCard(
        lastTopicTitle = "${lastTopic.category.title.substringBefore(":")}: ${lastTopic.title}",
        lastStepIndex = uiState.currentStepIndex + 1,
        totalSteps = uiState.steps.size.coerceAtLeast(1),
        completionPercentage = completionPercentage,
        accuracyPercentage = uiState.accuracyPercentage,
        currentStreak = uiState.currentStreak,
        totalPredictions = uiState.totalPredictions,
        onResume = { onNavigateTopic(lastTopic.id) }
      )

      // Post-Quiz Recommended Learning Path (if student has taken quiz)
      if (uiState.latestSuggestedTopicId != null && uiState.latestSuggestedTopicTitle != null) {
        Spacer(modifier = Modifier.height(14.dp))
        SuggestedPathBanner(
          topicTitle = uiState.latestSuggestedTopicTitle ?: "",
          reason = uiState.latestSuggestedTopicReason ?: "Recommended next stage based on your mastery checkpoint quiz.",
          scorePercent = uiState.latestMasteryScore,
          onContinue = { onNavigateTopic(uiState.latestSuggestedTopicId ?: "merge_sort_tree") }
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Subtle Review Recommendation (Spaced Repetition)
      SubtleReviewBanner(
        topicName = "Topic 4: Greedy vs DP Knapsack Proof",
        onReview = { onNavigateTopic("greedy_vs_dp_knapsack") }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 2.5 Featured Head-to-Head Comparative Sorting Lab
      ComparativeRaceBanner(
        onOpenRace = { onNavigateTopic("compare_algorithms") }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Algorithm Complexity Matrix Summary Deck (Best, Average, Worst & Space bounds)
      AlgorithmComplexitySummaryDeck(
        onNavigateTopic = onNavigateTopic
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Syllabus Curriculum Header & Category Filters (Solves Cluttering)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Curriculum Topics",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "8 Topics",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Horizontal Category Filter Chips to Declutter
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val categories = listOf("All", "Sorting & Trees", "DP & Greedy", "Graphs & Search", "Exam Bank")
        categories.forEach { cat ->
          FilterChip(
            selected = selectedCategoryFilter == cat,
            onClick = { selectedCategoryFilter = cat },
            label = {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (selectedCategoryFilter == cat) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selectedCategoryFilter == cat,
              borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
              selectedBorderColor = MaterialTheme.colorScheme.primary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Streamlined Topic Cards
      val allLaunchTopics = listOf(
        TopicLaunchItem(
          id = "bubble_sort",
          topicNumber = 1,
          categoryGroup = "Sorting & Trees",
          title = "Topic 1: Compare & Swap Sorting",
          subtitle = "Bubble Sort, Selection Sort, Custom Arrays & Inversions",
          complexity = "O(n²)/O(n)",
          icon = Icons.Default.CompareArrows,
          accentColor = MaterialTheme.colorScheme.primary
        ),
        TopicLaunchItem(
          id = "merge_sort_tree",
          topicNumber = 2,
          categoryGroup = "Sorting & Trees",
          title = "Topic 2: Recursion Trees & D&C",
          subtitle = "Merge Sort, Subproblem Slices, T(n)=2T(n/2)+O(n)",
          complexity = "O(n log n)",
          icon = Icons.Default.AccountTree,
          accentColor = Color(0xFF6366F1)
        ),
        TopicLaunchItem(
          id = "knapsack_01_dp",
          topicNumber = 3,
          categoryGroup = "DP & Greedy",
          title = "Topic 3: DP Table Grid & Traceback",
          subtitle = "0/1 Knapsack, Cell Dependencies & Optimal Path",
          complexity = "O(nW)",
          icon = Icons.Default.GridOn,
          accentColor = Color(0xFF0D9488)
        ),
        TopicLaunchItem(
          id = "greedy_vs_dp_knapsack",
          topicNumber = 4,
          categoryGroup = "DP & Greedy",
          title = "Topic 4: Greedy Proofs & Knapsack",
          subtitle = "Activity Selection, Greedy Choice Counterexamples",
          complexity = "Greedy vs DP",
          icon = Icons.Default.ElectricBolt,
          accentColor = Color(0xFFD97706)
        ),
        TopicLaunchItem(
          id = "dijkstra_graph",
          topicNumber = 5,
          categoryGroup = "Graphs & Search",
          title = "Topic 5: Graph Traversals & Shortest Paths",
          subtitle = "Dijkstra's Algorithm, Distance Priority Queue",
          complexity = "O(E log V)",
          icon = Icons.Default.Hub,
          accentColor = Color(0xFF8B5CF6)
        ),
        TopicLaunchItem(
          id = "n_queens_pruning",
          topicNumber = 6,
          categoryGroup = "Graphs & Search",
          title = "Topic 6: Pruning & Backtracking Trees",
          subtitle = "N-Queens State Space Search & Bounding Functions",
          complexity = "Backtracking",
          icon = Icons.Default.Shield,
          accentColor = Color(0xFFEC4899)
        ),
        TopicLaunchItem(
          id = "p_vs_np_map",
          topicNumber = 7,
          categoryGroup = "Exam Bank",
          title = "Topic 7: Complexity Theory & P vs NP",
          subtitle = "Polynomial Reductions & NP-Complete Classification",
          complexity = "Reductions",
          icon = Icons.Default.Psychology,
          accentColor = Color(0xFFF97316)
        ),
        TopicLaunchItem(
          id = "hand_trace_quiz",
          topicNumber = 8,
          categoryGroup = "Exam Bank",
          title = "Topic 8: ADA Exam Question Bank",
          subtitle = "University Hand-Trace Questions with Verification",
          complexity = "Exam Prep",
          icon = Icons.Default.Quiz,
          accentColor = Color(0xFF10B981)
        )
      )

      val filteredTopics = when (selectedCategoryFilter) {
        "All" -> allLaunchTopics
        else -> allLaunchTopics.filter { it.categoryGroup == selectedCategoryFilter }
      }

      // 4. Streamlined Topic Cards with proper consistent spacing
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        filteredTopics.forEach { topic ->
          val isCurrentTopic = (lastTopic.id == topic.id ||
            (lastTopic.id in listOf("bubble_sort", "selection_sort") && topic.id == "bubble_sort"))
          StreamlinedTopicCard(
            item = topic,
            isCurrentTopic = isCurrentTopic,
            completionPercentage = if (isCurrentTopic) completionPercentage else null,
            onClick = { onNavigateTopic(topic.id) }
          )
        }
      }

      // Generous bottom clearance padding to guarantee topics have clean, comfortable spacing when scrolled down
      Spacer(modifier = Modifier.height(48.dp))
    }
  }
}

data class TopicLaunchItem(
  val id: String,
  val topicNumber: Int,
  val categoryGroup: String,
  val title: String,
  val subtitle: String,
  val complexity: String,
  val icon: ImageVector,
  val accentColor: Color
)

/**
 * Clean, consolidated Active Focus card.
 * Integrates "Continue where you left off" with live mastery stats without clustering the screen.
 */
@Composable
private fun ActiveFocusCard(
  lastTopicTitle: String,
  lastStepIndex: Int,
  totalSteps: Int,
  completionPercentage: Int,
  accuracyPercentage: Int,
  currentStreak: Int,
  totalPredictions: Int,
  onResume: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("hero_resume_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Top status pill & Completion % Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CURRENT FOCUS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 10.sp
              )
            )
          }
        }

        // Prominent % Completion Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$completionPercentage% Complete",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = lastTopicTitle,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Interactive visualization, compare & swap animations, and predictive hand-tracing ready.",
        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Progress bar header with explicit completion %
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Step Progress",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
        Text(
          text = "$completionPercentage% • Step $lastStepIndex of $totalSteps",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Progress bar
      val progress = (completionPercentage / 100f).coerceIn(0f, 1f)
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Resume action button depicting progress
      Button(
        onClick = onResume,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("resume_topic_button")
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (completionPercentage >= 100) "Review Completed Topic" else "Resume Session ($completionPercentage% Done)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Compact 3-metric statistics row featuring Completion %
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Metric 1: Completion %
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
          ),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "$completionPercentage%",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Completion",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Metric 2: Accuracy
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
          ),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (totalPredictions > 0) "$accuracyPercentage%" else "—",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Accuracy",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Metric 3: Streak
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
          ),
          modifier = Modifier.weight(1f)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "$currentStreak",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Streak",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

/**
 * Subtle, non-intrusive Spaced Repetition card.
 * Replaces loud red warning boxes with an elegant prompt.
 */
@Composable
private fun SubtleReviewBanner(
  topicName: String,
  onReview: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Repeat,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Recommended Spaced Drill",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              fontSize = 10.sp
            )
          )
          Text(
            text = topicName,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      FilledTonalButton(
        onClick = onReview,
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(34.dp)
      ) {
        Text("Practice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

/**
 * Featured Head-to-Head Comparative Algorithm Race Lab Banner.
 */
@Composable
private fun ComparativeRaceBanner(
  onOpenRace: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("comparative_race_dashboard_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Head-to-Head Comparative Lab",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "NEW",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp
                  ),
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Live Side-by-Side Algorithm Benchmark",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Select any two sorting algorithms (QuickSort, MergeSort, BubbleSort, Selection, Insertion) and race them simultaneously on identical arrays with live animation and performance metrics.",
        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          ) {
            Text(
              text = "QuickSort vs MergeSort",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Button(
          onClick = onOpenRace,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier.testTag("launch_race_lab_button")
        ) {
          Text("Launch Lab ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Streamlined topic item with generous whitespace, high contrast, and complementary font colors.
 */
@Composable
private fun StreamlinedTopicCard(
  item: TopicLaunchItem,
  isCurrentTopic: Boolean = false,
  completionPercentage: Int? = null,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isCurrentTopic) 1.5.dp else 1.dp,
      color = if (isCurrentTopic) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
      } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentTopic) 2.dp else 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("launch_topic_${item.topicNumber}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top metadata row: Topic category tag + Complexity Chip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = item.accentColor.copy(alpha = 0.12f)
          ) {
            Text(
              text = "TOPIC ${item.topicNumber} • ${item.categoryGroup.uppercase()}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = item.accentColor
              ),
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
            )
          }

          if (isCurrentTopic) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = if (completionPercentage != null) "$completionPercentage% DONE" else "IN PROGRESS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          )
        ) {
          Text(
            text = item.complexity,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
              color = item.accentColor
            ),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Main content row: Avatar + Title + Subtitle + Action Arrow
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = item.accentColor.copy(alpha = 0.14f),
          modifier = Modifier.size(44.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = item.icon,
              contentDescription = null,
              tint = item.accentColor,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(3.dp))

          Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              lineHeight = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          shape = CircleShape,
          color = if (isCurrentTopic) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = "Open topic",
              tint = if (isCurrentTopic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Recommended learning path banner displayed on the Dashboard based on quiz performance.
 */
@Composable
private fun SuggestedPathBanner(
  topicTitle: String,
  reason: String,
  scorePercent: Int,
  onContinue: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_suggested_path_banner")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primary
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Explore,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "TAILORED LEARNING PATH",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 9.sp
              )
            )
          }
        }

        if (scorePercent > 0) {
          Text(
            text = "Quiz Score: $scorePercent%",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = topicTitle,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = reason,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(42.dp)
          .testTag("dashboard_resume_path_button")
      ) {
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Continue Along Learning Path",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}
