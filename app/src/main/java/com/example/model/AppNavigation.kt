package com.example.model

enum class TopicCategory(
  val title: String,
  val subtitle: String,
  val topicNumber: Int
) {
  TOPIC_1("Topic 1: Compare & Swap Sorting", "Bubble Sort, Selection Sort & Inversion Counting", 1),
  TOPIC_2("Topic 2: Recursion Trees & Recurrence", "Merge Sort, Binary Search & Master Theorem", 2),
  TOPIC_3("Topic 3: DP Table Grid & Traceback", "0/1 Knapsack, LCS & Optimal Reconstruction", 3),
  TOPIC_4("Topic 4: Greedy Decisions & Counterexamples", "Activity Selection & Greedy vs DP Proofs", 4),
  TOPIC_5("Topic 5: Graph Canvas & Shortest Path", "Dijkstra, Negative-Weight Edges & BFS/DFS", 5),
  TOPIC_6("Topic 6: Pruned Trees & Backtracking", "N-Queens State Space & Bounding Functions", 6),
  TOPIC_7("Topic 7: Complexity Theory & P vs NP", "Polynomial Reductions & NP-Completeness", 7),
  TOPIC_8("Topic 8: Exam Question Bank & Practice", "University Hand-Trace Questions & Quizzes", 8)
}

data class TopicItem(
  val id: String,
  val category: TopicCategory,
  val title: String,
  val badge: String,
  val description: String,
  val estimatedMinutes: Int = 15,
  val isCompleted: Boolean = false
)

object TopicCatalog {
  val allTopics = listOf(
    // Topic 1
    TopicItem(
      id = "bubble_sort",
      category = TopicCategory.TOPIC_1,
      title = "Bubble Sort Visualizer",
      badge = "O(n²)/O(n)",
      description = "Adjacent swap engine with adaptive early-exit detection.",
      estimatedMinutes = 15
    ),
    TopicItem(
      id = "selection_sort",
      category = TopicCategory.TOPIC_1,
      title = "Selection Sort Visualizer",
      badge = "O(n²)",
      description = "Min element placement with at most n-1 memory writes.",
      estimatedMinutes = 12
    ),
    TopicItem(
      id = "compare_algorithms",
      category = TopicCategory.TOPIC_1,
      title = "Head-to-Head Algorithm Race",
      badge = "Comparative Lab",
      description = "Side-by-side comparative animation of 2 sorting algorithms on identical datasets.",
      estimatedMinutes = 10
    ),
    // Topic 2
    TopicItem(
      id = "merge_sort_tree",
      category = TopicCategory.TOPIC_2,
      title = "Merge Sort Recursion Tree",
      badge = "O(n log n)",
      description = "Divide-and-conquer tree growing downward and collapsing on merge.",
      estimatedMinutes = 20
    ),
    TopicItem(
      id = "binary_search_tree",
      category = TopicCategory.TOPIC_2,
      title = "Binary Search Narrowing",
      badge = "O(log n)",
      description = "Single-path narrowing search space with mid calculations.",
      estimatedMinutes = 10
    ),
    // Topic 3
    TopicItem(
      id = "knapsack_01_dp",
      category = TopicCategory.TOPIC_3,
      title = "0/1 Knapsack DP Grid",
      badge = "O(nW)",
      description = "2D Dynamic Programming grid with dependency highlights & traceback.",
      estimatedMinutes = 25
    ),
    // Topic 4
    TopicItem(
      id = "greedy_vs_dp_knapsack",
      category = TopicCategory.TOPIC_4,
      title = "Greedy vs DP Knapsack",
      badge = "Counterexample",
      description = "Side-by-side demonstration of why greedy fails for 0/1 knapsack.",
      estimatedMinutes = 15
    ),
    TopicItem(
      id = "activity_selection_greedy",
      category = TopicCategory.TOPIC_4,
      title = "Activity Selection",
      badge = "O(n log n)",
      description = "Greedy earliest finish-time selection with compatibility checks.",
      estimatedMinutes = 15
    ),
    // Topic 5
    TopicItem(
      id = "dijkstra_graph",
      category = TopicCategory.TOPIC_5,
      title = "Dijkstra's Shortest Path",
      badge = "O(V²)/O(E log V)",
      description = "Interactive graph canvas with distance table and negative-edge demo.",
      estimatedMinutes = 25
    ),
    // Topic 6
    TopicItem(
      id = "n_queens_pruning",
      category = TopicCategory.TOPIC_6,
      title = "N-Queens Backtracking",
      badge = "Pruned Tree",
      description = "State space exploration with live chessboard and conflict reasons.",
      estimatedMinutes = 20
    ),
    // Topic 7
    TopicItem(
      id = "p_vs_np_map",
      category = TopicCategory.TOPIC_7,
      title = "P vs NP & Reductions",
      badge = "Classification",
      description = "Interactive Venn diagram of P, NP, NP-Complete, and NP-Hard problems.",
      estimatedMinutes = 15
    ),
    // Topic 8
    TopicItem(
      id = "hand_trace_quiz",
      category = TopicCategory.TOPIC_8,
      title = "Hand-Trace Exam Bank",
      badge = "Exam Practice",
      description = "University exam hand-trace problems verified against visualizers.",
      estimatedMinutes = 30
    )
  )
}
