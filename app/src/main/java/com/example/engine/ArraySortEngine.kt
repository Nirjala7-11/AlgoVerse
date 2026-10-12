package com.example.engine

import com.example.model.AlgorithmType
import com.example.model.ArrayPreset
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlin.random.Random

object ArraySortEngine {

  fun getPresetArray(preset: ArrayPreset): List<Int> {
    return when (preset) {
      ArrayPreset.ALREADY_SORTED -> listOf(6, 14, 22, 35, 48, 60, 75, 89)
      ArrayPreset.REVERSE_SORTED -> listOf(89, 75, 60, 48, 35, 22, 14, 6)
      ArrayPreset.ALL_DUPLICATES -> listOf(30, 30, 30, 30, 30, 30, 30, 30)
      ArrayPreset.SINGLE_ELEMENT -> listOf(42)
      ArrayPreset.RANDOM -> {
        val size = Random.nextInt(7, 11)
        val list = mutableListOf<Int>()
        repeat(size) {
          list.add(Random.nextInt(5, 96))
        }
        list
      }
    }
  }

  fun parseCustomArray(input: String): Result<List<Int>> {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
      return Result.failure(IllegalArgumentException("Please enter comma-separated numbers."))
    }

    val parts = trimmed.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.isEmpty()) {
      return Result.failure(IllegalArgumentException("No valid numbers found."))
    }

    if (parts.size > 30) {
      return Result.failure(IllegalArgumentException("Maximum 30 elements allowed for clear visual clarity (entered ${parts.size})."))
    }

    val numbers = mutableListOf<Int>()
    for (part in parts) {
      val parsed = part.toIntOrNull()
        ?: return Result.failure(IllegalArgumentException("Invalid number: '$part'"))
      if (parsed < 0 || parsed > 999) {
        return Result.failure(IllegalArgumentException("Numbers must be between 0 and 999 (got $parsed)."))
      }
      numbers.add(parsed)
    }

    return Result.success(numbers)
  }

  fun generateBubbleSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()

    var comparisons = 0
    var swaps = 0

    // Initial state step
    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting Bubble Sort on ${arr.size} elements."
      )
    )

    if (n <= 1) {
      if (n == 1) sortedIndices.add(0)
      steps.add(
        SortStep(
          stepIndex = 1,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.COMPLETE,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = 0,
          swapsCount = 0,
          remainingInversions = 0,
          codeLineNumber = 8,
          explanation = "Array with $n element is already sorted."
        )
      )
      return finalizeSteps(steps)
    }

    for (i in 0 until n - 1) {
      var swappedInThisPass = false

      for (j in 0 until n - i - 1) {
        comparisons++
        val valA = arr[j]
        val valB = arr[j + 1]
        val willSwap = valA > valB

        // Prediction & Comparison Step
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = j,
            secondaryIndex = j + 1,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Pass ${i + 1}: Comparing arr[$j] = $valA with arr[${j + 1}] = $valB.",
            requiresPrediction = true,
            predictPrompt = "Will arr[$j] ($valA) and arr[${j + 1}] ($valB) swap?",
            predictWillSwapOrUpdate = willSwap,
            predictOptionYesText = "Yes, Swap ($valA > $valB)",
            predictOptionNoText = "No, Keep Order ($valA ≤ $valB)",
            predictExplanation = if (willSwap) {
              "Correct! $valA is greater than $valB, so they must be swapped to achieve ascending order."
            } else {
              "Correct! $valA is less than or equal to $valB, so their relative order is already correct."
            }
          )
        )

        if (willSwap) {
          swaps++
          val temp = arr[j]
          arr[j] = arr[j + 1]
          arr[j + 1] = temp
          swappedInThisPass = true

          // Swapped Step
          steps.add(
            SortStep(
              stepIndex = steps.size,
              totalSteps = 0,
              arrayState = arr.toList(),
              actionType = StepActionType.SWAPPING,
              primaryIndex = j,
              secondaryIndex = j + 1,
              sortedIndices = sortedIndices.toSet(),
              comparisonsCount = comparisons,
              swapsCount = swaps,
              remainingInversions = ComplexityAnalyzer.countInversions(arr),
              codeLineNumber = 4,
              explanation = "Swapped arr[$j] ($valA) with arr[${j + 1}] ($valB).",
              requiresPrediction = false
            )
          )
        }
      }

      // Mark the end of the pass - largest element bubbled to n - 1 - i
      sortedIndices.add(n - 1 - i)
      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.ELEMENT_SORTED,
          primaryIndex = n - 1 - i,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 5,
          explanation = "End of Pass ${i + 1}: Element ${arr[n - 1 - i]} is now in its finalized sorted position."
        )
      )

      // Adaptive check: If no swaps occurred, the array is already sorted!
      if (!swappedInThisPass) {
        for (k in 0 until n) sortedIndices.add(k)
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.EARLY_EXIT,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = 0,
            codeLineNumber = 6,
            explanation = "Adaptive Early Exit! No swaps occurred during Pass ${i + 1}. Array is fully sorted in Best Case O(n)!"
          )
        )
        break
      }
    }

    // Mark all as sorted
    for (k in 0 until n) sortedIndices.add(k)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = 0,
        codeLineNumber = 7,
        explanation = "Bubble Sort Complete! Total: $comparisons comparisons, $swaps swaps."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateSelectionSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()

    var comparisons = 0
    var swaps = 0

    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting Selection Sort on ${arr.size} elements."
      )
    )

    if (n <= 1) {
      if (n == 1) sortedIndices.add(0)
      steps.add(
        SortStep(
          stepIndex = 1,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.COMPLETE,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = 0,
          swapsCount = 0,
          remainingInversions = 0,
          codeLineNumber = 8,
          explanation = "Array with $n element is already sorted."
        )
      )
      return finalizeSteps(steps)
    }

    for (i in 0 until n - 1) {
      var minIdx = i

      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.MIN_FOUND,
          primaryIndex = i,
          minCandidateIndex = minIdx,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 2,
          explanation = "Pass ${i + 1}: Initialized current minimum index minIdx = $i (value ${arr[i]})."
        )
      )

      for (j in i + 1 until n) {
        comparisons++
        val valJ = arr[j]
        val valMin = arr[minIdx]
        val isNewMin = valJ < valMin

        // Prediction for new minimum
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = j,
            secondaryIndex = minIdx,
            minCandidateIndex = minIdx,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Comparing candidate arr[$j] = $valJ with current min arr[$minIdx] = $valMin.",
            requiresPrediction = true,
            predictPrompt = "Is arr[$j] ($valJ) smaller than current min arr[$minIdx] ($valMin)?",
            predictWillSwapOrUpdate = isNewMin,
            predictOptionYesText = "Yes, New Min ($valJ < $valMin)",
            predictOptionNoText = "No, Keep Current Min ($valJ ≥ $valMin)",
            predictExplanation = if (isNewMin) {
              "Correct! $valJ is smaller than $valMin, so minIdx will update to $j."
            } else {
              "Correct! $valJ is not smaller than $valMin, so minIdx remains at $minIdx."
            }
          )
        )

        if (isNewMin) {
          minIdx = j
          steps.add(
            SortStep(
              stepIndex = steps.size,
              totalSteps = 0,
              arrayState = arr.toList(),
              actionType = StepActionType.MIN_FOUND,
              primaryIndex = j,
              minCandidateIndex = minIdx,
              sortedIndices = sortedIndices.toSet(),
              comparisonsCount = comparisons,
              swapsCount = swaps,
              remainingInversions = ComplexityAnalyzer.countInversions(arr),
              codeLineNumber = 4,
              explanation = "New minimum discovered: minIdx is now $minIdx (value ${arr[minIdx]})."
            )
          )
        }
      }

      // Check if swap needed at end of pass
      val needsSwap = minIdx != i
      if (needsSwap) {
        // Swap prediction
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = i,
            secondaryIndex = minIdx,
            minCandidateIndex = minIdx,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 5,
            explanation = "Minimum of unsorted subarray is arr[$minIdx] = ${arr[minIdx]}. Target slot is index $i (${arr[i]}).",
            requiresPrediction = true,
            predictPrompt = "Will min element ${arr[minIdx]} (index $minIdx) swap with index $i (${arr[i]})?",
            predictWillSwapOrUpdate = true,
            predictOptionYesText = "Yes, Swap to place min",
            predictOptionNoText = "No, Already in position",
            predictExplanation = "Correct! The minimum element found in this pass must be placed into index $i."
          )
        )

        swaps++
        val temp = arr[i]
        arr[i] = arr[minIdx]
        arr[minIdx] = temp

        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.SWAPPING,
            primaryIndex = i,
            secondaryIndex = minIdx,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 6,
            explanation = "Swapped minimum value ${arr[i]} into finalized slot $i."
          )
        )
      }

      sortedIndices.add(i)
      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.ELEMENT_SORTED,
          primaryIndex = i,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 7,
          explanation = "Index $i is now finalized with value ${arr[i]}."
        )
      )
    }

    for (k in 0 until n) sortedIndices.add(k)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = 0,
        codeLineNumber = 8,
        explanation = "Selection Sort Complete! Total: $comparisons comparisons, $swaps swaps."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateInsertionSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()
    var comparisons = 0
    var swaps = 0

    if (n > 0) sortedIndices.add(0)

    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting Insertion Sort on ${arr.size} elements. Index 0 is trivially sorted."
      )
    )

    for (i in 1 until n) {
      val key = arr[i]
      var j = i - 1

      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.MIN_FOUND,
          primaryIndex = i,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 2,
          explanation = "Pick arr[$i] = $key as key element to insert into sorted prefix [0..${i - 1}]."
        )
      )

      while (j >= 0) {
        comparisons++
        val leftVal = arr[j]

        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = j,
            secondaryIndex = j + 1,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Comparing key $key with prefix element arr[$j] = $leftVal."
          )
        )

        if (leftVal > key) {
          swaps++
          arr[j + 1] = leftVal

          steps.add(
            SortStep(
              stepIndex = steps.size,
              totalSteps = 0,
              arrayState = arr.toList(),
              actionType = StepActionType.SWAPPING,
              primaryIndex = j,
              secondaryIndex = j + 1,
              sortedIndices = sortedIndices.toSet(),
              comparisonsCount = comparisons,
              swapsCount = swaps,
              remainingInversions = ComplexityAnalyzer.countInversions(arr),
              codeLineNumber = 4,
              explanation = "Shifted arr[$j] ($leftVal) right to index ${j + 1}."
            )
          )
          j--
        } else {
          break
        }
      }

      arr[j + 1] = key
      sortedIndices.add(i)

      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.ELEMENT_SORTED,
          primaryIndex = j + 1,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 5,
          explanation = "Inserted key $key into position ${j + 1}."
        )
      )
    }

    for (k in 0 until n) sortedIndices.add(k)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = 0,
        codeLineNumber = 6,
        explanation = "Insertion Sort Complete! Total: $comparisons comparisons, $swaps shifts."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateQuickSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()
    var comparisons = 0
    var swaps = 0

    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting QuickSort on ${arr.size} elements with Lomuto partition."
      )
    )

    fun quickSortInternal(low: Int, high: Int) {
      if (low < high) {
        val pivot = arr[high]
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.MIN_FOUND,
            primaryIndex = high,
            minCandidateIndex = high,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 2,
            explanation = "Selected pivot arr[$high] = $pivot for partition range [$low, $high]."
          )
        )

        var i = low - 1
        for (j in low until high) {
          comparisons++
          steps.add(
            SortStep(
              stepIndex = steps.size,
              totalSteps = 0,
              arrayState = arr.toList(),
              actionType = StepActionType.COMPARING,
              primaryIndex = j,
              secondaryIndex = high,
              sortedIndices = sortedIndices.toSet(),
              comparisonsCount = comparisons,
              swapsCount = swaps,
              remainingInversions = ComplexityAnalyzer.countInversions(arr),
              codeLineNumber = 3,
              explanation = "Comparing arr[$j] (${arr[j]}) with pivot $pivot."
            )
          )

          if (arr[j] < pivot) {
            i++
            if (i != j) {
              swaps++
              val temp = arr[i]
              arr[i] = arr[j]
              arr[j] = temp

              steps.add(
                SortStep(
                  stepIndex = steps.size,
                  totalSteps = 0,
                  arrayState = arr.toList(),
                  actionType = StepActionType.SWAPPING,
                  primaryIndex = i,
                  secondaryIndex = j,
                  sortedIndices = sortedIndices.toSet(),
                  comparisonsCount = comparisons,
                  swapsCount = swaps,
                  remainingInversions = ComplexityAnalyzer.countInversions(arr),
                  codeLineNumber = 4,
                  explanation = "Partition: Swapped arr[$i] (${arr[i]}) with arr[$j] (${arr[j]})."
                )
              )
            }
          }
        }

        // Place pivot
        swaps++
        val pIndex = i + 1
        val temp = arr[pIndex]
        arr[pIndex] = arr[high]
        arr[high] = temp

        sortedIndices.add(pIndex)
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.ELEMENT_SORTED,
            primaryIndex = pIndex,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 5,
            explanation = "Placed pivot $pivot into its finalized partition index $pIndex."
          )
        )

        quickSortInternal(low, pIndex - 1)
        quickSortInternal(pIndex + 1, high)
      } else if (low == high && low in arr.indices) {
        sortedIndices.add(low)
      }
    }

    if (n > 1) {
      quickSortInternal(0, n - 1)
    }

    for (k in 0 until n) sortedIndices.add(k)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = 0,
        codeLineNumber = 6,
        explanation = "QuickSort Complete! Total: $comparisons comparisons, $swaps partition swaps."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateMergeSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()
    var comparisons = 0
    var writes = 0

    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        comparisonsCount = comparisons,
        swapsCount = writes,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting Merge Sort on ${arr.size} elements using Divide & Conquer."
      )
    )

    fun merge(low: Int, mid: Int, high: Int) {
      val left = arr.subList(low, mid + 1).toList()
      val right = arr.subList(mid + 1, high + 1).toList()

      var i = 0
      var j = 0
      var k = low

      while (i < left.size && j < right.size) {
        comparisons++
        val leftVal = left[i]
        val rightVal = right[j]

        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = low + i,
            secondaryIndex = mid + 1 + j,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = writes,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 2,
            explanation = "Merge: Comparing left item $leftVal with right item $rightVal."
          )
        )

        writes++
        if (leftVal <= rightVal) {
          arr[k] = leftVal
          i++
        } else {
          arr[k] = rightVal
          j++
        }

        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.SWAPPING,
            primaryIndex = k,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = writes,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Wrote merged element ${arr[k]} into position $k."
          )
        )
        k++
      }

      while (i < left.size) {
        writes++
        arr[k] = left[i]
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.SWAPPING,
            primaryIndex = k,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = writes,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 4,
            explanation = "Flushed remaining left element ${arr[k]} to position $k."
          )
        )
        i++
        k++
      }

      while (j < right.size) {
        writes++
        arr[k] = right[j]
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.SWAPPING,
            primaryIndex = k,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = writes,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 4,
            explanation = "Flushed remaining right element ${arr[k]} to position $k."
          )
        )
        j++
        k++
      }

      if (low == 0 && high == n - 1) {
        for (idx in 0 until n) sortedIndices.add(idx)
      }
    }

    fun mergeSortInternal(low: Int, high: Int) {
      if (low < high) {
        val mid = (low + high) / 2
        mergeSortInternal(low, mid)
        mergeSortInternal(mid + 1, high)
        merge(low, mid, high)
      }
    }

    if (n > 1) {
      mergeSortInternal(0, n - 1)
    }

    for (k in 0 until n) sortedIndices.add(k)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = writes,
        remainingInversions = 0,
        codeLineNumber = 5,
        explanation = "Merge Sort Complete! Total: $comparisons comparisons, $writes auxiliary writes."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateHeapSortSteps(initialArray: List<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val arr = initialArray.toMutableList()
    val n = arr.size
    val sortedIndices = mutableSetOf<Int>()
    var comparisons = 0
    var swaps = 0

    // Initial Start Step
    steps.add(
      SortStep(
        stepIndex = 0,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.START,
        sortedIndices = emptySet(),
        comparisonsCount = 0,
        swapsCount = 0,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 1,
        explanation = "Starting Heap Sort on $n elements. Phase 1: Build Max-Heap from bottom up."
      )
    )

    fun heapify(heapSize: Int, rootIdx: Int) {
      var largest = rootIdx
      val left = 2 * rootIdx + 1
      val right = 2 * rootIdx + 2

      // Check left child
      if (left < heapSize) {
        comparisons++
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = rootIdx,
            secondaryIndex = left,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Heapify at node $rootIdx (${arr[rootIdx]}): Comparing with Left Child index $left (${arr[left]})."
          )
        )
        if (arr[left] > arr[largest]) {
          largest = left
        }
      }

      // Check right child
      if (right < heapSize) {
        comparisons++
        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.COMPARING,
            primaryIndex = largest,
            secondaryIndex = right,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 3,
            explanation = "Heapify at node $rootIdx: Comparing current largest (${arr[largest]}) with Right Child index $right (${arr[right]})."
          )
        )
        if (arr[right] > arr[largest]) {
          largest = right
        }
      }

      // If largest is not root
      if (largest != rootIdx) {
        val temp = arr[rootIdx]
        arr[rootIdx] = arr[largest]
        arr[largest] = temp
        swaps++

        steps.add(
          SortStep(
            stepIndex = steps.size,
            totalSteps = 0,
            arrayState = arr.toList(),
            actionType = StepActionType.SWAPPING,
            primaryIndex = rootIdx,
            secondaryIndex = largest,
            sortedIndices = sortedIndices.toSet(),
            comparisonsCount = comparisons,
            swapsCount = swaps,
            remainingInversions = ComplexityAnalyzer.countInversions(arr),
            codeLineNumber = 4,
            explanation = "Max-Heap violation: Child index $largest (${arr[rootIdx]}) > parent index $rootIdx ($temp). Swapped!"
          )
        )

        // Recursively heapify the affected subtree
        heapify(heapSize, largest)
      }
    }

    // Phase 1: Build Max-Heap (start from last non-leaf node down to 0)
    for (i in (n / 2 - 1) downTo 0) {
      heapify(n, i)
    }

    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.MIN_FOUND,
        primaryIndex = 0,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = ComplexityAnalyzer.countInversions(arr),
        codeLineNumber = 2,
        explanation = "Max-Heap established! Root arr[0] = ${arr[0]} is guaranteed maximum element. Phase 2: Extract."
      )
    )

    // Phase 2: One by one extract elements from heap
    for (i in n - 1 downTo 1) {
      // Swap current root (maximum) to end of unsorted boundary
      val maxVal = arr[0]
      val endVal = arr[i]
      arr[0] = endVal
      arr[i] = maxVal
      swaps++
      sortedIndices.add(i)

      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.SWAPPING,
          primaryIndex = 0,
          secondaryIndex = i,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 5,
          explanation = "Extracted Max ($maxVal) to sorted index $i. Swapped with $endVal. Re-heapifying root."
        )
      )

      steps.add(
        SortStep(
          stepIndex = steps.size,
          totalSteps = 0,
          arrayState = arr.toList(),
          actionType = StepActionType.ELEMENT_SORTED,
          primaryIndex = i,
          sortedIndices = sortedIndices.toSet(),
          comparisonsCount = comparisons,
          swapsCount = swaps,
          remainingInversions = ComplexityAnalyzer.countInversions(arr),
          codeLineNumber = 5,
          explanation = "Element index $i ($maxVal) is finalized in its sorted position."
        )
      )

      // Call max heapify on the reduced heap
      heapify(i, 0)
    }

    sortedIndices.add(0)
    steps.add(
      SortStep(
        stepIndex = steps.size,
        totalSteps = 0,
        arrayState = arr.toList(),
        actionType = StepActionType.COMPLETE,
        sortedIndices = sortedIndices.toSet(),
        comparisonsCount = comparisons,
        swapsCount = swaps,
        remainingInversions = 0,
        codeLineNumber = 6,
        explanation = "Heap Sort Complete! Total: $comparisons comparisons, $swaps swaps. O(n log n) in-place achieved."
      )
    )

    return finalizeSteps(steps)
  }

  fun generateSteps(algorithm: AlgorithmType, initialArray: List<Int>): List<SortStep> {
    return when (algorithm) {
      AlgorithmType.BUBBLE_SORT -> generateBubbleSortSteps(initialArray)
      AlgorithmType.SELECTION_SORT -> generateSelectionSortSteps(initialArray)
      AlgorithmType.INSERTION_SORT -> generateInsertionSortSteps(initialArray)
      AlgorithmType.MERGE_SORT -> generateMergeSortSteps(initialArray)
      AlgorithmType.QUICK_SORT -> generateQuickSortSteps(initialArray)
      AlgorithmType.HEAP_SORT -> generateHeapSortSteps(initialArray)
    }
  }

  private fun finalizeSteps(steps: List<SortStep>): List<SortStep> {
    val total = steps.size
    return steps.mapIndexed { idx, step ->
      step.copy(stepIndex = idx, totalSteps = total)
    }
  }
}
