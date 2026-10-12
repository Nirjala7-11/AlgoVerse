package com.example.model

enum class AlgorithmType(
  val displayName: String,
  val bestCase: String,
  val averageCase: String,
  val worstCase: String,
  val spaceComplexity: String,
  val isAdaptive: Boolean,
  val isStable: Boolean
) {
  BUBBLE_SORT(
    displayName = "Bubble Sort",
    bestCase = "O(n)",
    averageCase = "O(n²)",
    worstCase = "O(n²)",
    spaceComplexity = "O(1)",
    isAdaptive = true,
    isStable = true
  ),
  SELECTION_SORT(
    displayName = "Selection Sort",
    bestCase = "O(n²)",
    averageCase = "O(n²)",
    worstCase = "O(n²)",
    spaceComplexity = "O(1)",
    isAdaptive = false,
    isStable = false
  ),
  INSERTION_SORT(
    displayName = "Insertion Sort",
    bestCase = "O(n)",
    averageCase = "O(n²)",
    worstCase = "O(n²)",
    spaceComplexity = "O(1)",
    isAdaptive = true,
    isStable = true
  ),
  MERGE_SORT(
    displayName = "Merge Sort",
    bestCase = "O(n log n)",
    averageCase = "O(n log n)",
    worstCase = "O(n log n)",
    spaceComplexity = "O(n)",
    isAdaptive = false,
    isStable = true
  ),
  QUICK_SORT(
    displayName = "Quick Sort",
    bestCase = "O(n log n)",
    averageCase = "O(n log n)",
    worstCase = "O(n²)",
    spaceComplexity = "O(log n)",
    isAdaptive = false,
    isStable = false
  ),
  HEAP_SORT(
    displayName = "Heap Sort",
    bestCase = "O(n log n)",
    averageCase = "O(n log n)",
    worstCase = "O(n log n)",
    spaceComplexity = "O(1)",
    isAdaptive = false,
    isStable = false
  )
}

enum class StepActionType {
  START,
  COMPARING,
  SWAPPING,
  MIN_FOUND,
  ELEMENT_SORTED,
  EARLY_EXIT,
  COMPLETE
}

enum class ComplexityRegion(
  val label: String,
  val bigO: String,
  val badgeColorHex: Long,
  val description: String
) {
  BEST_CASE(
    label = "Best Case",
    bigO = "O(n)",
    badgeColorHex = 0xFF10B981, // Emerald Green
    description = "Array is already sorted (0 inversions). Bubble Sort finishes in 1 pass!"
  ),
  AVERAGE_CASE(
    label = "Average Case",
    bigO = "O(n²)",
    badgeColorHex = 0xFFF59E0B, // Amber
    description = "Partially ordered array with random inversions. Requires ~n²/2 comparisons."
  ),
  WORST_CASE(
    label = "Worst Case",
    bigO = "O(n²)",
    badgeColorHex = 0xFFEF4444, // Red
    description = "Reverse sorted or maximal inversions n(n-1)/2. Maximum comparisons & swaps!"
  ),
  TRIVIAL_CASE(
    label = "Trivial Case",
    bigO = "O(1)",
    badgeColorHex = 0xFF3B82F6, // Blue
    description = "Single element or empty array requires 0 comparisons."
  )
}

enum class ArrayPreset(val displayName: String) {
  ALREADY_SORTED("Already sorted"),
  REVERSE_SORTED("Reverse sorted"),
  ALL_DUPLICATES("All duplicates"),
  SINGLE_ELEMENT("Single element"),
  RANDOM("Random")
}

data class SortStep(
  val stepIndex: Int,
  val totalSteps: Int,
  val arrayState: List<Int>,
  val actionType: StepActionType,
  val primaryIndex: Int = -1,
  val secondaryIndex: Int = -1,
  val sortedIndices: Set<Int> = emptySet(),
  val minCandidateIndex: Int = -1,
  val comparisonsCount: Int = 0,
  val swapsCount: Int = 0,
  val remainingInversions: Int = 0,
  val codeLineNumber: Int = 1,
  val explanation: String = "",
  // Predict prompt data for predict-before-reveal mode
  val requiresPrediction: Boolean = false,
  val predictPrompt: String = "",
  val predictWillSwapOrUpdate: Boolean = false,
  val predictOptionYesText: String = "Yes, Swap!",
  val predictOptionNoText: String = "No, Keep Order",
  val predictExplanation: String = ""
)
