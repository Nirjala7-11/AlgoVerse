package com.example

import com.example.engine.ArraySortEngine
import com.example.model.AlgorithmType
import com.example.model.StepActionType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testComparativeSortingEngines_sortCorrectly() {
    val testArray = listOf(42, 17, 93, 8, 25, 61, 3, 50)
    val expected = testArray.sorted()

    // Test QuickSort
    val quickSteps = ArraySortEngine.generateSteps(AlgorithmType.QUICK_SORT, testArray)
    assertTrue("QuickSort steps should not be empty", quickSteps.isNotEmpty())
    val quickFinal = quickSteps.last()
    assertEquals(StepActionType.COMPLETE, quickFinal.actionType)
    assertEquals(expected, quickFinal.arrayState)

    // Test MergeSort
    val mergeSteps = ArraySortEngine.generateSteps(AlgorithmType.MERGE_SORT, testArray)
    assertTrue("MergeSort steps should not be empty", mergeSteps.isNotEmpty())
    val mergeFinal = mergeSteps.last()
    assertEquals(StepActionType.COMPLETE, mergeFinal.actionType)
    assertEquals(expected, mergeFinal.arrayState)

    // Test InsertionSort
    val insertionSteps = ArraySortEngine.generateSteps(AlgorithmType.INSERTION_SORT, testArray)
    assertTrue("InsertionSort steps should not be empty", insertionSteps.isNotEmpty())
    val insertionFinal = insertionSteps.last()
    assertEquals(StepActionType.COMPLETE, insertionFinal.actionType)
    assertEquals(expected, insertionFinal.arrayState)
  }

  @Test
  fun testQuickSort_vs_BubbleSort_efficiency() {
    val testArray = listOf(80, 70, 60, 50, 40, 30, 20, 10) // Reverse sorted
    val bubbleSteps = ArraySortEngine.generateSteps(AlgorithmType.BUBBLE_SORT, testArray)
    val quickSteps = ArraySortEngine.generateSteps(AlgorithmType.QUICK_SORT, testArray)

    val bubbleFinal = bubbleSteps.last()
    val quickFinal = quickSteps.last()

    // Both should reach sorted order
    assertEquals(testArray.sorted(), bubbleFinal.arrayState)
    assertEquals(testArray.sorted(), quickFinal.arrayState)
    assertTrue("Bubble sort swaps should be recorded", bubbleFinal.swapsCount > 0)
    assertTrue("Quick sort swaps should be recorded", quickFinal.swapsCount > 0)
  }
}

