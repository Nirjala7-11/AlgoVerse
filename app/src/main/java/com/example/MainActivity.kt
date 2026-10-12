package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AlgorithmType
import com.example.ui.components.AlgoVerseSidebarContent
import com.example.ui.components.SocraticChatbotSheet
import com.example.ui.screens.ComparativeSortingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.Engine1Screen
import com.example.ui.screens.Engine2RecursionScreen
import com.example.ui.screens.Engine3DpScreen
import com.example.ui.screens.Engine4GreedyScreen
import com.example.ui.screens.Engine5GraphScreen
import com.example.ui.screens.Engine6PruningScreen
import com.example.ui.screens.ExamBankScreen
import com.example.ui.screens.Unit8TheoryScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.viewmodel.AlgoVerseViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val systemDark = isSystemInDarkTheme()
      var isDarkMode by remember { mutableStateOf(systemDark) }

      MyApplicationTheme(darkTheme = isDarkMode) {
        AlgoVerseApp(
          isDarkMode = isDarkMode,
          onToggleTheme = { isDarkMode = !isDarkMode }
        )
      }
    }
  }
}

@Composable
fun AlgoVerseApp(
  isDarkMode: Boolean = false,
  onToggleTheme: () -> Unit = {}
) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val viewModel: AlgoVerseViewModel = viewModel()

  // Default landing destination is Dashboard (Learning Hub & Progress)
  var currentTopicId by remember { mutableStateOf("dashboard") }
  var lastStudiedTopicId by remember { mutableStateOf("bubble_sort") }
  var showAiTutorSheet by remember { mutableStateOf(false) }

  BackHandler(enabled = drawerState.isOpen || currentTopicId != "dashboard") {
    if (drawerState.isOpen) {
      scope.launch { drawerState.close() }
    } else {
      currentTopicId = "dashboard"
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      AlgoVerseSidebarContent(
        selectedTopicId = currentTopicId,
        onSelectTopic = { topicId ->
          currentTopicId = topicId
          if (topicId != "dashboard") {
            lastStudiedTopicId = topicId
          }
          when (topicId) {
            "bubble_sort" -> viewModel.setAlgorithm(AlgorithmType.BUBBLE_SORT)
            "selection_sort" -> viewModel.setAlgorithm(AlgorithmType.SELECTION_SORT)
            "insertion_sort" -> viewModel.setAlgorithm(AlgorithmType.INSERTION_SORT)
            "merge_sort" -> viewModel.setAlgorithm(AlgorithmType.MERGE_SORT)
            "quick_sort" -> viewModel.setAlgorithm(AlgorithmType.QUICK_SORT)
            "heap_sort" -> viewModel.setAlgorithm(AlgorithmType.HEAP_SORT)
          }
        },
        onClose = {
          scope.launch { drawerState.close() }
        },
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme
      )
    }
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      when (currentTopicId) {
        "dashboard" -> {
          DashboardScreen(
            viewModel = viewModel,
            onNavigateTopic = { topicId ->
              currentTopicId = topicId
              if (topicId != "dashboard") {
                lastStudiedTopicId = topicId
              }
              when (topicId) {
                "bubble_sort" -> viewModel.setAlgorithm(AlgorithmType.BUBBLE_SORT)
                "selection_sort" -> viewModel.setAlgorithm(AlgorithmType.SELECTION_SORT)
                "insertion_sort" -> viewModel.setAlgorithm(AlgorithmType.INSERTION_SORT)
                "merge_sort" -> viewModel.setAlgorithm(AlgorithmType.MERGE_SORT)
                "quick_sort" -> viewModel.setAlgorithm(AlgorithmType.QUICK_SORT)
                "heap_sort" -> viewModel.setAlgorithm(AlgorithmType.HEAP_SORT)
              }
            },
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onOpenAiTutor = { showAiTutorSheet = true },
            lastTopicId = lastStudiedTopicId,
            isDarkMode = isDarkMode,
            onToggleTheme = onToggleTheme
          )
        }

        "compare_algorithms", "algorithm_race" -> {
          ComparativeSortingScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "bubble_sort", "selection_sort", "insertion_sort", "merge_sort", "quick_sort", "heap_sort" -> {
          Engine1Screen(
            viewModel = viewModel,
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" },
            onNavigateTopic = { topicId ->
              currentTopicId = topicId
              if (topicId != "dashboard") {
                lastStudiedTopicId = topicId
              }
              when (topicId) {
                "bubble_sort" -> viewModel.setAlgorithm(AlgorithmType.BUBBLE_SORT)
                "selection_sort" -> viewModel.setAlgorithm(AlgorithmType.SELECTION_SORT)
                "insertion_sort" -> viewModel.setAlgorithm(AlgorithmType.INSERTION_SORT)
                "merge_sort" -> viewModel.setAlgorithm(AlgorithmType.MERGE_SORT)
                "quick_sort" -> viewModel.setAlgorithm(AlgorithmType.QUICK_SORT)
                "heap_sort" -> viewModel.setAlgorithm(AlgorithmType.HEAP_SORT)
              }
            }
          )
        }

        "merge_sort_tree", "binary_search_tree" -> {
          Engine2RecursionScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "knapsack_01_dp", "lcs_dp" -> {
          Engine3DpScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "activity_selection_greedy", "greedy_vs_dp_knapsack" -> {
          Engine4GreedyScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "dijkstra_graph", "bfs_dfs_graph" -> {
          Engine5GraphScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "n_queens_pruning" -> {
          Engine6PruningScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "p_vs_np_map" -> {
          Unit8TheoryScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" }
          )
        }

        "hand_trace_quiz" -> {
          ExamBankScreen(
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onBackToDashboard = { currentTopicId = "dashboard" },
            onNavigateTopic = { topicId ->
              currentTopicId = topicId
              lastStudiedTopicId = topicId
            }
          )
        }

        else -> {
          DashboardScreen(
            viewModel = viewModel,
            onNavigateTopic = { topicId ->
              currentTopicId = topicId
              lastStudiedTopicId = topicId
            },
            onOpenSidebar = { scope.launch { drawerState.open() } },
            onOpenAiTutor = { showAiTutorSheet = true },
            lastTopicId = lastStudiedTopicId
          )
        }
      }
    }

    if (showAiTutorSheet) {
      SocraticChatbotSheet(
        onDismiss = { showAiTutorSheet = false }
      )
    }
  }
}
