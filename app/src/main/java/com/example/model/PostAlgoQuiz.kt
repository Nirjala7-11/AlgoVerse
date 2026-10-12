package com.example.model

data class QuizQuestion(
  val id: Int,
  val prompt: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String,
  val conceptTag: String
)

enum class MasteryTier {
  FOUNDATION,
  PROFICIENT,
  ADVANCED
}

enum class LearningActionType {
  REPEAT_PRACTICE,
  ADVANCE_SYLLABUS,
  CHALLENGE_EXAM
}

data class QuizResult(
  val totalQuestions: Int,
  val correctCount: Int,
  val scorePercent: Int,
  val tier: MasteryTier,
  val tierTitle: String,
  val tierDescription: String,
  val suggestedTopicId: String,
  val suggestedTopicTitle: String,
  val suggestedTopicSubtitle: String,
  val suggestedActionLabel: String,
  val actionType: LearningActionType
)

object PostAlgoQuizGenerator {

  fun generateQuiz(
    algorithm: AlgorithmType,
    array: List<Int>,
    totalSwaps: Int,
    totalComparisons: Int
  ): List<QuizQuestion> {
    val n = array.size
    val largest = array.maxOrNull() ?: 100
    val smallest = array.minOrNull() ?: 0

    return when (algorithm) {
      AlgorithmType.BUBBLE_SORT -> listOf(
        QuizQuestion(
          id = 1,
          prompt = "In the array of size N = $n you just sorted, what happens to the largest element ($largest) during Pass 1?",
          options = listOf(
            "It is placed at index 0 immediately",
            "It moves to the last index (N - 1) through consecutive adjacent swaps",
            "It remains in its original position until the final pass",
            "It is deleted and re-inserted"
          ),
          correctIndex = 1,
          explanation = "Bubble Sort continuously compares adjacent elements. The largest unsorted element ($largest) is guaranteed to bubble all the way to the end in Pass 1.",
          conceptTag = "Pass 1 Invariant"
        ),
        QuizQuestion(
          id = 2,
          prompt = "Every adjacent swap in Bubble Sort resolves EXACTLY how many array inversions?",
          options = listOf(
            "Exactly 1 inversion",
            "At least 2 inversions",
            "log(n) inversions",
            "All remaining inversions simultaneously"
          ),
          correctIndex = 0,
          explanation = "Because Bubble Sort only swaps immediately adjacent elements (arr[j] and arr[j+1]), it only affects that single relative pair order, resolving exactly 1 inversion per swap.",
          conceptTag = "Inversion Theorem"
        ),
        QuizQuestion(
          id = 3,
          prompt = "If the array was already sorted in ascending order, how can Bubble Sort be optimized to finish in O(n) linear time?",
          options = listOf(
            "By sorting backward from right to left",
            "By tracking a boolean 'swapped' flag and halting if no swap occurs in Pass 1",
            "By skipping every second comparison",
            "Bubble Sort can never finish in O(n) under any condition"
          ),
          correctIndex = 1,
          explanation = "If a pass completes with 0 swaps executed, the array is already sorted! An optimized Bubble Sort checks this flag and halts after 1 pass, achieving O(n) best-case complexity.",
          conceptTag = "Best-Case Optimization"
        )
      )

      AlgorithmType.SELECTION_SORT -> listOf(
        QuizQuestion(
          id = 1,
          prompt = "In Selection Sort on an array of size N = $n, what is the MAXIMUM number of write/swap operations performed across the entire algorithm?",
          options = listOf(
            "O(n²) swaps in the worst case",
            "At most N - 1 swaps (at most 1 swap per outer pass)",
            "O(n log n) swaps",
            "Zero swaps"
          ),
          correctIndex = 1,
          explanation = "A key advantage of Selection Sort is that it minimizes memory writes: it only performs at most ONE swap per outer loop iteration, totaling at most N - 1 writes!",
          conceptTag = "Write Minimization"
        ),
        QuizQuestion(
          id = 2,
          prompt = "Why is standard Selection Sort considered an UNSTABLE sorting algorithm?",
          options = listOf(
            "It uses recursive call stacks that can overflow",
            "Long-distance swaps can exchange an element past an identical duplicate, reversing their original order",
            "It cannot handle negative numbers",
            "It runs in non-deterministic random time"
          ),
          correctIndex = 1,
          explanation = "Because Selection Sort swaps the minimum element across long distances, it can displace an identical duplicate element behind another, violating stability.",
          conceptTag = "Stability Analysis"
        ),
        QuizQuestion(
          id = 3,
          prompt = "Unlike Bubble Sort, does standard Selection Sort ever perform fewer than n(n-1)/2 comparisons when given an already sorted array?",
          options = listOf(
            "Yes, it finishes in O(n) comparisons",
            "No, it ALWAYS scans the entire unsorted partition to find the minimum, taking Θ(n²) comparisons even if sorted",
            "Yes, it finishes in O(log n) comparisons",
            "It does not perform comparisons"
          ),
          correctIndex = 1,
          explanation = "Selection Sort has no early-exit condition for comparisons. It must exhaustively scan all remaining elements to verify the true minimum, making its best, average, and worst comparisons all Θ(n²).",
          conceptTag = "Comparison Lower Bound"
        )
      )

      else -> listOf(
        QuizQuestion(
          id = 1,
          prompt = "What is the primary recurrence relation solved by Merge Sort / Divide and Conquer?",
          options = listOf("T(n) = 2T(n/2) + O(n)", "T(n) = T(n-1) + O(1)", "T(n) = T(n/2) + O(1)", "T(n) = 4T(n/2) + O(n²)"),
          correctIndex = 0,
          explanation = "Merge Sort splits the array into two halves 2T(n/2) and merges in linear time O(n), evaluating to O(n log n).",
          conceptTag = "Divide & Conquer"
        ),
        QuizQuestion(
          id = 2,
          prompt = "What partition scheme places the pivot into its finalized rank while placing smaller items left?",
          options = listOf("Lomuto / Hoare Partition", "Binary Insertion", "Greedy Interval", "Dynamic Memoization"),
          correctIndex = 0,
          explanation = "Partitioning is the fundamental sub-routine of QuickSort that places the pivot in its finalized index.",
          conceptTag = "Partitioning"
        ),
        QuizQuestion(
          id = 3,
          prompt = "Which of the following describes the best case time complexity of Insertion Sort?",
          options = listOf("O(n) on already sorted input", "O(n²)", "O(log n)", "O(n!)"),
          correctIndex = 0,
          explanation = "When an array has 0 inversions, Insertion Sort only performs 1 comparison per element and 0 shifts, finishing in O(n).",
          conceptTag = "Adaptive Sorting"
        )
      )
    }
  }

  fun evaluateResult(
    algorithm: AlgorithmType,
    correctCount: Int,
    totalQuestions: Int
  ): QuizResult {
    val percent = if (totalQuestions > 0) ((correctCount.toFloat() / totalQuestions) * 100).toInt() else 0

    return when {
      percent >= 85 -> QuizResult(
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        scorePercent = percent,
        tier = MasteryTier.ADVANCED,
        tierTitle = "Mastery Level Reached 🏆",
        tierDescription = "Outstanding! You demonstrated mastery of algorithm mechanics, time complexities, and stability trade-offs.",
        suggestedTopicId = "hand_trace_quiz",
        suggestedTopicTitle = "Topic 8: ADA Exam Question Bank",
        suggestedTopicSubtitle = "Test your mastery on University Hand-Trace Exam questions with Visualizer verification.",
        suggestedActionLabel = "Challenge Exam Bank ➔",
        actionType = LearningActionType.CHALLENGE_EXAM
      )

      percent >= 60 -> QuizResult(
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        scorePercent = percent,
        tier = MasteryTier.PROFICIENT,
        tierTitle = "Proficient Understanding 🌟",
        tierDescription = "Solid grasp of quadratic sorting fundamentals! You are ready to transition to Divide & Conquer.",
        suggestedTopicId = "merge_sort_tree",
        suggestedTopicTitle = "Topic 2: Recursion Trees & D&C",
        suggestedTopicSubtitle = "Discover how Merge Sort slices problems to break past the O(n²) barrier into O(n log n).",
        suggestedActionLabel = "Advance to Topic 2 ➔",
        actionType = LearningActionType.ADVANCE_SYLLABUS
      )

      else -> QuizResult(
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        scorePercent = percent,
        tier = MasteryTier.FOUNDATION,
        tierTitle = "Foundation Review Recommended 💡",
        tierDescription = "You're getting familiar with the concepts! A quick review of inversion counts and predict mode will solidify your foundation.",
        suggestedTopicId = if (algorithm == AlgorithmType.BUBBLE_SORT) "bubble_sort" else "selection_sort",
        suggestedTopicTitle = "Topic 1: Predict & Inversion Practice",
        suggestedTopicSubtitle = "Enable Predict Mode on a Reverse Sorted array to master adjacent comparison dynamics.",
        suggestedActionLabel = "Practice with Inversions ➔",
        actionType = LearningActionType.REPEAT_PRACTICE
      )
    }
  }
}
