package com.example.engine

import com.example.model.AlgorithmType
import com.example.model.ComplexityRegion

object ComplexityAnalyzer {

  fun countInversions(array: List<Int>): Int {
    var inversions = 0
    val n = array.size
    for (i in 0 until n) {
      for (j in i + 1 until n) {
        if (array[i] > array[j]) {
          inversions++
        }
      }
    }
    return inversions
  }

  fun determineInitialComplexityRegion(
    algorithm: AlgorithmType,
    array: List<Int>
  ): ComplexityRegion {
    val n = array.size
    if (n <= 1) return ComplexityRegion.TRIVIAL_CASE

    val maxInversions = n * (n - 1) / 2
    val inversions = countInversions(array)
    val isAllDuplicates = array.distinct().size == 1

    return when (algorithm) {
      AlgorithmType.BUBBLE_SORT -> {
        when {
          isAllDuplicates || inversions == 0 -> ComplexityRegion.BEST_CASE
          inversions == maxInversions -> ComplexityRegion.WORST_CASE
          else -> ComplexityRegion.AVERAGE_CASE
        }
      }
      AlgorithmType.SELECTION_SORT -> {
        // Selection sort always does n*(n-1)/2 comparisons!
        // But for swaps, 0 swaps is best case, n-1 swaps is worst case.
        when {
          isAllDuplicates || inversions == 0 -> ComplexityRegion.BEST_CASE
          inversions == maxInversions -> ComplexityRegion.WORST_CASE
          else -> ComplexityRegion.AVERAGE_CASE
        }
      }
      AlgorithmType.INSERTION_SORT -> {
        when {
          isAllDuplicates || inversions == 0 -> ComplexityRegion.BEST_CASE
          inversions == maxInversions -> ComplexityRegion.WORST_CASE
          else -> ComplexityRegion.AVERAGE_CASE
        }
      }
      AlgorithmType.MERGE_SORT -> {
        // Merge Sort always divides evenly into n log n
        ComplexityRegion.BEST_CASE
      }
      AlgorithmType.QUICK_SORT -> {
        when {
          inversions == 0 || inversions == maxInversions -> ComplexityRegion.WORST_CASE // Unbalanced partitions with end pivot
          else -> ComplexityRegion.AVERAGE_CASE
        }
      }
      AlgorithmType.HEAP_SORT -> {
        // Heap sort always performs O(n log n) in all distributions
        ComplexityRegion.BEST_CASE
      }
    }
  }

  fun getDynamicComplexityStatus(
    algorithm: AlgorithmType,
    initialRegion: ComplexityRegion,
    remainingInversions: Int,
    comparisons: Int,
    swaps: Int,
    n: Int,
    isComplete: Boolean
  ): Pair<ComplexityRegion, String> {
    if (n <= 1) return Pair(ComplexityRegion.TRIVIAL_CASE, "Trivial array of size $n")

    val maxPossibleComparisons = n * (n - 1) / 2

    return when (algorithm) {
      AlgorithmType.BUBBLE_SORT -> {
        if (isComplete) {
          if (swaps == 0 && comparisons <= n - 1) {
            Pair(ComplexityRegion.BEST_CASE, "Completed in Best Case O(n): 0 swaps, early exit detected!")
          } else if (comparisons >= maxPossibleComparisons && swaps >= maxPossibleComparisons) {
            Pair(ComplexityRegion.WORST_CASE, "Completed in Worst Case O(n²): Full comparisons ($comparisons) & maximum swaps ($swaps)!")
          } else {
            Pair(ComplexityRegion.AVERAGE_CASE, "Completed in Average Case O(n²): $comparisons comparisons, $swaps swaps.")
          }
        } else {
          when (initialRegion) {
            ComplexityRegion.BEST_CASE -> {
              if (swaps > 0) {
                Pair(ComplexityRegion.AVERAGE_CASE, "Swaps occurred; shifted from Best Case to Average Case O(n²)")
              } else {
                Pair(ComplexityRegion.BEST_CASE, "Tracking Best Case O(n): No swaps needed so far ($comparisons comparisons)")
              }
            }
            ComplexityRegion.WORST_CASE -> {
              Pair(ComplexityRegion.WORST_CASE, "Worst Case O(n²): Reverse sorted, $remainingInversions remaining inversions to resolve")
            }
            else -> {
              Pair(ComplexityRegion.AVERAGE_CASE, "Average Case O(n²): Resolving inversions ($remainingInversions left)")
            }
          }
        }
      }
      AlgorithmType.SELECTION_SORT -> {
        if (isComplete) {
          if (swaps == 0) {
            Pair(ComplexityRegion.BEST_CASE, "Selection Sort: Best Case swaps (0 swaps), but still requires O(n²) comparisons ($comparisons)")
          } else if (swaps >= n - 1) {
            Pair(ComplexityRegion.WORST_CASE, "Selection Sort: Worst Case ($swaps swaps, $comparisons comparisons = O(n²))")
          } else {
            Pair(ComplexityRegion.AVERAGE_CASE, "Selection Sort: Average Case ($swaps swaps, $comparisons comparisons)")
          }
        } else {
          when (initialRegion) {
            ComplexityRegion.BEST_CASE -> Pair(ComplexityRegion.BEST_CASE, "Comparing items without swapping: O(n²) comparisons, 0 swaps")
            ComplexityRegion.WORST_CASE -> Pair(ComplexityRegion.WORST_CASE, "Reverse input: $swaps swaps made, $comparisons comparisons so far")
            else -> Pair(ComplexityRegion.AVERAGE_CASE, "Progressing: $comparisons/$maxPossibleComparisons comparisons done")
          }
        }
      }
      AlgorithmType.INSERTION_SORT -> {
        if (isComplete) {
          if (swaps == 0) {
            Pair(ComplexityRegion.BEST_CASE, "Insertion Sort: Best Case O(n) achieved! Already ordered, only $comparisons comparisons.")
          } else {
            Pair(ComplexityRegion.AVERAGE_CASE, "Insertion Sort: Completed with $comparisons comparisons and $swaps shifts.")
          }
        } else {
          Pair(ComplexityRegion.AVERAGE_CASE, "Insertion Sort: Progressing through sorted prefix insertion...")
        }
      }
      AlgorithmType.MERGE_SORT -> {
        if (isComplete) {
          Pair(ComplexityRegion.BEST_CASE, "Merge Sort: Completed in Θ(n log n) with $comparisons comparisons and $swaps writes!")
        } else {
          Pair(ComplexityRegion.BEST_CASE, "Merge Sort: Divide & Conquer merging subarrays (Θ(n log n))")
        }
      }
      AlgorithmType.QUICK_SORT -> {
        if (isComplete) {
          Pair(ComplexityRegion.AVERAGE_CASE, "Quick Sort: Partitioning complete with $comparisons comparisons and $swaps partition swaps.")
        } else {
          Pair(ComplexityRegion.AVERAGE_CASE, "Quick Sort: Partitioning subarrays around pivot...")
        }
      }
      AlgorithmType.HEAP_SORT -> {
        if (isComplete) {
          Pair(ComplexityRegion.BEST_CASE, "Heap Sort: Completed in Θ(n log n) with $comparisons comparisons and $swaps swaps in O(1) space!")
        } else {
          Pair(ComplexityRegion.BEST_CASE, "Heap Sort: Building/Maintaining binary max-heap property (Θ(n log n))")
        }
      }
    }
  }
}
