package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.StudentMastery
import com.example.engine.ArraySortEngine
import com.example.engine.ComplexityAnalyzer
import com.example.model.AlgorithmType
import com.example.model.ArrayPreset
import com.example.model.ComplexityRegion
import com.example.model.PostAlgoQuizGenerator
import com.example.model.QuizQuestion
import com.example.model.QuizResult
import com.example.model.SortStep
import com.example.model.StepActionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PredictFeedback(
  val stepIndex: Int,
  val userSelectedYes: Boolean,
  val isCorrect: Boolean,
  val explanation: String
)

data class AlgoVerseUiState(
  val currentAlgorithm: AlgorithmType = AlgorithmType.BUBBLE_SORT,
  val rawInputText: String = "24, 7, 36, 12, 5, 19, 42, 1",
  val inputErrorMessage: String? = null,
  val currentArray: List<Int> = listOf(24, 7, 36, 12, 5, 19, 42, 1),
  val steps: List<SortStep> = emptyList(),
  val currentStepIndex: Int = 0,
  val isPlaying: Boolean = false,
  val playbackSpeedMs: Long = 800L,
  val predictModeEnabled: Boolean = false,
  val activePredictionFeedback: PredictFeedback? = null,
  val answeredStepIndices: Set<Int> = emptySet(),
  val initialComplexityRegion: ComplexityRegion = ComplexityRegion.AVERAGE_CASE,
  val currentComplexityRegion: ComplexityRegion = ComplexityRegion.AVERAGE_CASE,
  val complexityExplanation: String = "",
  val totalPredictions: Int = 0,
  val correctPredictions: Int = 0,
  val currentStreak: Int = 0,
  val bestStreak: Int = 0,
  val selectedTab: Int = 0, // 0 = Visualizer & Predict, 1 = Dual Compare, 2 = Theory & Big-O
  val dualBubbleSteps: List<SortStep> = emptyList(),
  val dualSelectionSteps: List<SortStep> = emptyList(),
  val isQuizSheetOpen: Boolean = false,
  val quizQuestions: List<QuizQuestion> = emptyList(),
  val quizCurrentIndex: Int = 0,
  val quizSelectedAnswers: Map<Int, Int> = emptyMap(),
  val quizCompleted: Boolean = false,
  val quizResult: QuizResult? = null,
  val latestSuggestedTopicId: String? = null,
  val latestSuggestedTopicTitle: String? = null,
  val latestSuggestedTopicReason: String? = null,
  val latestMasteryScore: Int = 0
) {
  val currentStep: SortStep?
    get() = if (steps.isNotEmpty() && currentStepIndex in steps.indices) steps[currentStepIndex] else null

  val accuracyPercentage: Int
    get() = if (totalPredictions > 0) ((correctPredictions.toFloat() / totalPredictions) * 100).toInt() else 0

  val isAtStart: Boolean get() = currentStepIndex == 0
  val isAtEnd: Boolean get() = steps.isNotEmpty() && currentStepIndex == steps.size - 1
}

class AlgoVerseViewModel(application: Application) : AndroidViewModel(application) {

  private val database: AppDatabase = Room.databaseBuilder(
    application,
    AppDatabase::class.java,
    "algoverse_mastery.db"
  ).fallbackToDestructiveMigration().build()

  private val masteryDao = database.masteryDao()

  private val _uiState = MutableStateFlow(AlgoVerseUiState())
  val uiState: StateFlow<AlgoVerseUiState> = _uiState.asStateFlow()

  private var playbackJob: Job? = null

  init {
    loadMastery()
    applyCustomArray("24, 7, 36, 12, 5, 19, 42, 1")
  }

  private fun loadMastery() {
    viewModelScope.launch {
      val key = _uiState.value.currentAlgorithm.name
      val mastery = masteryDao.getMastery(key)
      if (mastery != null) {
        _uiState.update {
          it.copy(
            totalPredictions = mastery.totalPredictions,
            correctPredictions = mastery.correctPredictions,
            currentStreak = mastery.currentStreak,
            bestStreak = mastery.bestStreak,
            latestMasteryScore = mastery.quizScorePercent,
            latestSuggestedTopicId = mastery.suggestedNextTopicId
          )
        }
      }
    }
  }

  private fun persistMastery() {
    val state = _uiState.value
    viewModelScope.launch {
      val key = state.currentAlgorithm.name
      val existing = masteryDao.getMastery(key)
      masteryDao.upsertMastery(
        StudentMastery(
          algorithmKey = key,
          totalPredictions = state.totalPredictions,
          correctPredictions = state.correctPredictions,
          currentStreak = state.currentStreak,
          bestStreak = state.bestStreak,
          arraysSortedCount = existing?.arraysSortedCount ?: 0,
          quizScorePercent = existing?.quizScorePercent ?: state.latestMasteryScore,
          quizCompletedCount = existing?.quizCompletedCount ?: 0,
          suggestedNextTopicId = state.latestSuggestedTopicId ?: (existing?.suggestedNextTopicId ?: "merge_sort_tree")
        )
      )
    }
  }

  fun openQuiz() {
    val state = _uiState.value
    val lastStep = state.steps.lastOrNull()
    val totalSwaps = lastStep?.swapsCount ?: 0
    val totalComparisons = lastStep?.comparisonsCount ?: 0
    val questions = PostAlgoQuizGenerator.generateQuiz(
      algorithm = state.currentAlgorithm,
      array = state.currentArray,
      totalSwaps = totalSwaps,
      totalComparisons = totalComparisons
    )
    _uiState.update {
      it.copy(
        isQuizSheetOpen = true,
        quizQuestions = questions,
        quizCurrentIndex = 0,
        quizSelectedAnswers = emptyMap(),
        quizCompleted = false,
        quizResult = null
      )
    }
  }

  fun closeQuiz() {
    _uiState.update { it.copy(isQuizSheetOpen = false) }
  }

  fun selectQuizAnswer(questionId: Int, optionIndex: Int) {
    _uiState.update {
      it.copy(quizSelectedAnswers = it.quizSelectedAnswers + (questionId to optionIndex))
    }
  }

  fun nextQuizQuestion() {
    val state = _uiState.value
    if (state.quizCurrentIndex < state.quizQuestions.size - 1) {
      _uiState.update { it.copy(quizCurrentIndex = it.quizCurrentIndex + 1) }
    } else {
      completeQuiz()
    }
  }

  fun completeQuiz() {
    val state = _uiState.value
    var correctCount = 0
    state.quizQuestions.forEach { q ->
      if (state.quizSelectedAnswers[q.id] == q.correctIndex) {
        correctCount++
      }
    }

    val result = PostAlgoQuizGenerator.evaluateResult(
      algorithm = state.currentAlgorithm,
      correctCount = correctCount,
      totalQuestions = state.quizQuestions.size
    )

    _uiState.update {
      it.copy(
        quizCompleted = true,
        quizResult = result,
        latestSuggestedTopicId = result.suggestedTopicId,
        latestSuggestedTopicTitle = result.suggestedTopicTitle,
        latestSuggestedTopicReason = result.tierDescription,
        latestMasteryScore = result.scorePercent
      )
    }

    // Persist quiz mastery to Room Database
    viewModelScope.launch {
      val key = state.currentAlgorithm.name
      val existing = masteryDao.getMastery(key)
      masteryDao.upsertMastery(
        StudentMastery(
          algorithmKey = key,
          totalPredictions = state.totalPredictions,
          correctPredictions = state.correctPredictions,
          currentStreak = state.currentStreak,
          bestStreak = state.bestStreak,
          arraysSortedCount = (existing?.arraysSortedCount ?: 0) + 1,
          quizScorePercent = result.scorePercent,
          quizCompletedCount = (existing?.quizCompletedCount ?: 0) + 1,
          suggestedNextTopicId = result.suggestedTopicId
        )
      )
    }
  }

  fun setAlgorithm(algorithm: AlgorithmType) {
    if (_uiState.value.currentAlgorithm == algorithm) return
    pause()
    _uiState.update { it.copy(currentAlgorithm = algorithm) }
    recalculateSteps(_uiState.value.currentArray)
    loadMastery()
  }

  fun setSelectedTab(tabIndex: Int) {
    _uiState.update { it.copy(selectedTab = tabIndex) }
  }

  fun selectPreset(preset: ArrayPreset) {
    pause()
    val newArray = ArraySortEngine.getPresetArray(preset)
    val formatted = newArray.joinToString(", ")
    _uiState.update {
      it.copy(
        rawInputText = formatted,
        inputErrorMessage = null
      )
    }
    applyCustomArray(formatted)
  }

  fun onInputTextChanged(text: String) {
    _uiState.update { it.copy(rawInputText = text) }
  }

  fun applyCustomInput() {
    applyCustomArray(_uiState.value.rawInputText)
  }

  private fun applyCustomArray(inputStr: String) {
    pause()
    val parsedResult = ArraySortEngine.parseCustomArray(inputStr)
    parsedResult.onSuccess { array ->
      _uiState.update {
        it.copy(
          currentArray = array,
          inputErrorMessage = null,
          activePredictionFeedback = null,
          answeredStepIndices = emptySet()
        )
      }
      recalculateSteps(array)
    }.onFailure { error ->
      _uiState.update {
        it.copy(inputErrorMessage = error.message ?: "Invalid array input")
      }
    }
  }

  private fun recalculateSteps(array: List<Int>) {
    val algorithm = _uiState.value.currentAlgorithm
    val steps = ArraySortEngine.generateSteps(algorithm, array)

    val initialRegion = ComplexityAnalyzer.determineInitialComplexityRegion(algorithm, array)
    val remainingInversions = ComplexityAnalyzer.countInversions(array)
    val (dynRegion, dynExplanation) = ComplexityAnalyzer.getDynamicComplexityStatus(
      algorithm = algorithm,
      initialRegion = initialRegion,
      remainingInversions = remainingInversions,
      comparisons = 0,
      swaps = 0,
      n = array.size,
      isComplete = false
    )

    // Also calculate both for Dual Compare tab
    val dualBubble = ArraySortEngine.generateBubbleSortSteps(array)
    val dualSelection = ArraySortEngine.generateSelectionSortSteps(array)

    _uiState.update {
      it.copy(
        steps = steps,
        currentStepIndex = 0,
        initialComplexityRegion = initialRegion,
        currentComplexityRegion = dynRegion,
        complexityExplanation = dynExplanation,
        activePredictionFeedback = null,
        dualBubbleSteps = dualBubble,
        dualSelectionSteps = dualSelection
      )
    }
  }

  fun togglePredictMode() {
    _uiState.update { it.copy(predictModeEnabled = !it.predictModeEnabled) }
  }

  fun submitPrediction(userChoseYes: Boolean) {
    val state = _uiState.value
    val step = state.currentStep ?: return
    if (!step.requiresPrediction) return

    val isCorrect = userChoseYes == step.predictWillSwapOrUpdate
    val newTotal = state.totalPredictions + 1
    val newCorrect = if (isCorrect) state.correctPredictions + 1 else state.correctPredictions
    val newStreak = if (isCorrect) state.currentStreak + 1 else 0
    val newBest = maxOf(state.bestStreak, newStreak)

    val feedback = PredictFeedback(
      stepIndex = state.currentStepIndex,
      userSelectedYes = userChoseYes,
      isCorrect = isCorrect,
      explanation = step.predictExplanation
    )

    _uiState.update {
      it.copy(
        activePredictionFeedback = feedback,
        answeredStepIndices = it.answeredStepIndices + state.currentStepIndex,
        totalPredictions = newTotal,
        correctPredictions = newCorrect,
        currentStreak = newStreak,
        bestStreak = newBest
      )
    }
    persistMastery()
  }

  fun dismissPredictionAndContinue() {
    _uiState.update { it.copy(activePredictionFeedback = null) }
    stepForward()
  }

  fun play() {
    if (_uiState.value.isAtEnd) {
      seekTo(0)
    }
    _uiState.update { it.copy(isPlaying = true) }
    startPlaybackTicker()
  }

  fun pause() {
    playbackJob?.cancel()
    playbackJob = null
    _uiState.update { it.copy(isPlaying = false) }
  }

  fun stepForward() {
    val state = _uiState.value
    if (state.currentStepIndex < state.steps.size - 1) {
      val nextIndex = state.currentStepIndex + 1
      seekTo(nextIndex)
    } else {
      pause()
    }
  }

  fun stepBackward() {
    val state = _uiState.value
    if (state.currentStepIndex > 0) {
      val prevIndex = state.currentStepIndex - 1
      seekTo(prevIndex)
    }
  }

  fun reset() {
    pause()
    seekTo(0)
  }

  fun jumpToEnd() {
    pause()
    if (_uiState.value.steps.isNotEmpty()) {
      seekTo(_uiState.value.steps.size - 1)
    }
  }

  fun seekTo(index: Int) {
    val state = _uiState.value
    if (state.steps.isEmpty()) return
    val clamped = index.coerceIn(0, state.steps.size - 1)
    val step = state.steps[clamped]

    val isComplete = step.actionType == StepActionType.COMPLETE || step.actionType == StepActionType.EARLY_EXIT
    val (dynRegion, dynExplanation) = ComplexityAnalyzer.getDynamicComplexityStatus(
      algorithm = state.currentAlgorithm,
      initialRegion = state.initialComplexityRegion,
      remainingInversions = step.remainingInversions,
      comparisons = step.comparisonsCount,
      swaps = step.swapsCount,
      n = state.currentArray.size,
      isComplete = isComplete
    )

    // Check if we hit a predict step that has not been answered yet
    val needsPredictionPause = state.predictModeEnabled &&
        step.requiresPrediction &&
        !state.answeredStepIndices.contains(clamped)

    if (needsPredictionPause && state.isPlaying) {
      pause()
    }

    _uiState.update {
      it.copy(
        currentStepIndex = clamped,
        currentComplexityRegion = dynRegion,
        complexityExplanation = dynExplanation,
        activePredictionFeedback = if (needsPredictionPause) null else it.activePredictionFeedback
      )
    }
  }

  fun setPlaybackSpeed(delayMs: Long) {
    _uiState.update { it.copy(playbackSpeedMs = delayMs) }
    if (_uiState.value.isPlaying) {
      startPlaybackTicker()
    }
  }

  private fun startPlaybackTicker() {
    playbackJob?.cancel()
    playbackJob = viewModelScope.launch {
      while (_uiState.value.isPlaying) {
        val state = _uiState.value
        val step = state.currentStep

        // Check if predict mode should pause
        if (state.predictModeEnabled &&
          step != null &&
          step.requiresPrediction &&
          !state.answeredStepIndices.contains(state.currentStepIndex)
        ) {
          pause()
          break
        }

        delay(state.playbackSpeedMs)

        if (_uiState.value.isAtEnd) {
          pause()
          break
        } else {
          val nextIndex = _uiState.value.currentStepIndex + 1
          seekTo(nextIndex)
        }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    playbackJob?.cancel()
  }
}
