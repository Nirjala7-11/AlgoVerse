package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ai.ChatMessage
import com.example.ai.GeminiChatbotService
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocraticChatbotSheet(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  // Personalized Student Learning Goals
  val learningGoals = listOf(
    "🎓 Exam Prep",
    "💻 Interview Prep",
    "🔍 Deep Proofs",
    "⚡ Fast Intuition"
  )
  var selectedGoal by remember { mutableStateOf(learningGoals[0]) }

  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        id = "welcome",
        role = "model",
        text = """
🎯 Core Concept
Hello! I am your AlgoVerse Socratic ADA Mentor. Rather than solving assignments for you, I guide your intuition through algorithm proofs, recurrences, and edge cases.

💡 Step-by-Step Intuition
• Tell me what topic you're exploring (e.g., Heap Sort, Dijkstra negative edges, or Master Theorem).
• I'll break down the invariant, walk through step logic, and test your understanding with guided questions.

🧠 Guided Socratic Challenge
What specific algorithm or proof doubt would you like to explore today?
""".trimIndent()
      )
    )
  }

  val context = LocalContext.current
  var inputText by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var isListening by remember { mutableStateOf(false) }
  var speechFeedback by remember { mutableStateOf<String?>(null) }
  var showVoicePrompts by remember { mutableStateOf(false) }

  // Auto-scroll when new messages arrive
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Check if scrolled away from bottom
  val showScrollToBottom by remember {
    derivedStateOf {
      val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
      messages.isNotEmpty() && lastVisible < messages.size - 1
    }
  }

  // Speech Recognition Activity Result Launcher
  val speechRecognitionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isListening = false
    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val spokenText = matches?.firstOrNull()?.trim()
      if (!spokenText.isNullOrBlank()) {
        inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
        speechFeedback = "Heard: \"$spokenText\""
      } else {
        speechFeedback = "No speech detected. Please speak clearly into your mic."
      }
    } else {
      speechFeedback = null
    }
  }

  // Audio Permission Request Launcher
  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask AlgoVerse Socratic AI Mentor...")
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
      }
      try {
        isListening = true
        speechFeedback = "Listening for your algorithm question..."
        speechRecognitionLauncher.launch(intent)
      } catch (e: ActivityNotFoundException) {
        isListening = false
        speechFeedback = "Speech recognizer unavailable. Tap any spoken voice prompt below:"
        showVoicePrompts = true
      }
    } else {
      isListening = false
      speechFeedback = "Microphone permission is required for voice input."
      Toast.makeText(context, "Microphone permission is required for speech input.", Toast.LENGTH_SHORT).show()
    }
  }

  fun startVoiceInput() {
    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      return
    }

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
      putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask AlgoVerse Socratic AI Mentor...")
      putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }

    try {
      isListening = true
      speechFeedback = "Listening for your algorithm question..."
      speechRecognitionLauncher.launch(intent)
    } catch (e: ActivityNotFoundException) {
      isListening = false
      speechFeedback = "Speech recognizer unavailable. Tap any spoken prompt below:"
      showVoicePrompts = true
    }
  }

  val voiceQuickQueries = listOf(
    "How does Heap Sort build max-heap in O(n)?",
    "Why does Dijkstra fail on negative edges?",
    "Explain Master Theorem case 2",
    "Why does Bubble Sort take O(n²)?"
  )

  val suggestions = listOf(
    "How does Heap Sort build heap in O(n)?",
    "Why does Dijkstra fail on negative edges?",
    "Why does Greedy fail on 0/1 Knapsack?",
    "Explain Master Theorem for Merge Sort",
    "How does N-Queens bounding function work?"
  )

  fun sendMessage(promptText: String) {
    if (promptText.isBlank() || isLoading) return
    val userMsg = ChatMessage(id = UUID.randomUUID().toString(), role = "user", text = promptText.trim())
    messages.add(userMsg)
    inputText = ""
    isLoading = true

    scope.launch {
      listState.animateScrollToItem(messages.size - 1)
      val responseText = GeminiChatbotService.sendMessage(
        history = messages.toList(),
        newPrompt = promptText.trim(),
        learningGoal = selectedGoal
      )
      val aiMsg = ChatMessage(id = UUID.randomUUID().toString(), role = "model", text = responseText)
      messages.add(aiMsg)
      isLoading = false
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A),
    contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    modifier = modifier.fillMaxHeight(0.92f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .navigationBarsPadding()
        .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              "AlgoVerse Socratic Mentor",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
              "Personalized Gemini 3.5 • Structured ADA Guidance",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF38BDF8)
            )
          }
        }

        Row {
          IconButton(
            onClick = {
              messages.clear()
              messages.add(
                ChatMessage(
                  id = "welcome",
                  role = "model",
                  text = """
🎯 Core Concept
Chat cleared! I am ready to guide your intuition on your next algorithm challenge.

💡 Step-by-Step Intuition
• Pick a personalized learning goal below or ask any recurrence / correctness proof question.

🧠 Guided Socratic Challenge
What topic would you like to explore next?
""".trimIndent()
                )
              )
            }
          ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = Color(0xFF94A3B8))
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Personalized Goal Selector Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        learningGoals.forEach { goal ->
          val isSelected = selectedGoal == goal
          FilterChip(
            selected = isSelected,
            onClick = { selectedGoal = goal },
            label = {
              Text(
                goal,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.height(30.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Suggestion Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        suggestions.forEach { suggestion ->
          FilterChip(
            selected = false,
            onClick = { sendMessage(suggestion) },
            label = { Text(suggestion, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)) },
            colors = FilterChipDefaults.filterChipColors(
              containerColor = Color(0xFF131D31),
              labelColor = Color(0xFFCBD5E1)
            ),
            modifier = Modifier.height(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Message Thread with Smooth Scrolling and Jump to Bottom
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        LazyColumn(
          state = listState,
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(messages, key = { it.id }) { msg ->
            val isUser = msg.role == "user"
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
            ) {
              if (!isUser) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38BDF8))
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
              }

              if (isUser) {
                Surface(
                  shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = 14.dp,
                    bottomEnd = 2.dp
                  ),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                  Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = Color.White,
                      lineHeight = 20.sp
                    ),
                    modifier = Modifier.padding(12.dp)
                  )
                }
              } else {
                // Formatted Structured Socratic AI Bubble
                StructuredAiMessageBubble(
                  text = msg.text,
                  modifier = Modifier.fillMaxWidth(0.92f)
                )
              }
            }
          }

          if (isLoading) {
            item {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 34.dp, top = 4.dp)
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(18.dp),
                  color = Color(0xFF38BDF8),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing algorithm proofs & recurrences...", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
              }
            }
          }
        }

        // Floating Scroll-To-Bottom button
        if (showScrollToBottom) {
          FloatingActionButton(
            onClick = {
              scope.launch {
                listState.animateScrollToItem(messages.size - 1)
              }
            },
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(bottom = 8.dp, end = 4.dp)
              .size(36.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(2.dp)
          ) {
            Icon(Icons.Default.ArrowDownward, contentDescription = "Scroll to Bottom", modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Listening Animation Visualizer Banner
      AnimatedVisibility(
        visible = isListening,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF1E293B),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .testTag("ai_mentor_listening_indicator")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Listening",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(16.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Text(
                  text = "Listening... Speak your algorithm doubt",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color.White)
                )
                Text(
                  text = "Converting your voice to text...",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF94A3B8)
                )
              }
            }

            IconButton(
              onClick = {
                isListening = false
                speechFeedback = null
              },
              modifier = Modifier.size(26.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Listening",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      // Speech Feedback Banner
      if (speechFeedback != null && !isListening) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF0F243A),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = speechFeedback ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0)),
                maxLines = 2
              )
            }
            IconButton(
              onClick = { speechFeedback = null },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      // Voice Quick Query Prompt Chips
      AnimatedVisibility(visible = showVoicePrompts) {
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
          Text(
            text = "Spoken Question Presets (Tap to dictate):",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF38BDF8),
            modifier = Modifier.padding(bottom = 4.dp)
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            voiceQuickQueries.forEach { prompt ->
              FilterChip(
                selected = false,
                onClick = {
                  inputText = prompt
                  showVoicePrompts = false
                  speechFeedback = "Selected: \"$prompt\""
                },
                label = { Text(prompt, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)) },
                colors = FilterChipDefaults.filterChipColors(
                  containerColor = Color(0xFF1E293B),
                  labelColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.height(28.dp)
              )
            }
          }
        }
      }

      // Input Field & Action Buttons Row (Flush with bottom navigation bar)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = { Text("Ask a doubt or tap mic to speak...", color = Color(0xFF64748B), fontSize = 13.sp) },
          modifier = Modifier
            .weight(1f)
            .testTag("ai_mentor_input_field"),
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1E293B),
            unfocusedContainerColor = Color(0xFF1E293B),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          maxLines = 3
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Speech-to-Text Microphone Button
        IconButton(
          onClick = {
            if (isListening) {
              isListening = false
            } else {
              startVoiceInput()
            }
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (isListening) Color(0xFFEF4444) else Color(0xFF1E293B))
            .border(1.dp, if (isListening) Color.White else Color(0xFF38BDF8), CircleShape)
            .testTag("ai_mentor_mic_button")
        ) {
          Icon(
            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isListening) "Stop Listening" else "Speech to Text Input",
            tint = if (isListening) Color.White else Color(0xFF38BDF8),
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Send Button
        IconButton(
          onClick = { sendMessage(inputText) },
          enabled = inputText.isNotBlank() && !isLoading,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.primary else Color(0xFF334155))
            .testTag("ai_mentor_send_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

private fun formatMarkdownInline(raw: String): androidx.compose.ui.text.AnnotatedString {
  val clean = raw.trim()
  return buildAnnotatedString {
    var index = 0
    while (index < clean.length) {
      if (clean.startsWith("**", index)) {
        val end = clean.indexOf("**", index + 2)
        if (end != -1) {
          withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
            append(clean.substring(index + 2, end))
          }
          index = end + 2
          continue
        }
      }
      if (clean.startsWith("`", index)) {
        val end = clean.indexOf("`", index + 1)
        if (end != -1) {
          withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8), background = Color(0xFF0F172A))) {
            append(" ${clean.substring(index + 1, end)} ")
          }
          index = end + 1
          continue
        }
      }
      append(clean[index])
      index++
    }
  }
}

/**
 * Renders structured Socratic AI responses with highlighted badges, bullet points,
 * and clear typographical hierarchy.
 */
@Composable
private fun StructuredAiMessageBubble(
  text: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(
      topStart = 14.dp,
      topEnd = 14.dp,
      bottomStart = 2.dp,
      bottomEnd = 14.dp
    ),
    color = Color(0xFF1E293B),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      val lines = text.lines()
      var i = 0
      while (i < lines.size) {
        val line = lines[i]
        val cleanLine = line.trim().removePrefix("#").removePrefix("#").removePrefix("#").trim()
        val lower = cleanLine.lowercase()
        when {
          lower.contains("core concept") || cleanLine.startsWith("🎯") -> {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeaderBadge(title = "🎯 Core Concept", badgeColor = Color(0xFF38BDF8))
            val remainder = cleanLine.substringAfter("Core Concept").substringAfter("🎯").removePrefix(":").removePrefix("**").trim()
            if (remainder.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = formatMarkdownInline(remainder),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 19.sp),
                modifier = Modifier.padding(vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
          lower.contains("step-by-step intuition") || lower.contains("intuition") || cleanLine.startsWith("💡") -> {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeaderBadge(title = "💡 Intuition & Step-by-Step", badgeColor = Color(0xFF10B981))
            val remainder = cleanLine.substringAfter("Intuition").substringAfter("💡").removePrefix(":").removePrefix("**").trim()
            if (remainder.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = formatMarkdownInline(remainder),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 19.sp),
                modifier = Modifier.padding(vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
          lower.contains("edge case") || lower.contains("traps") || cleanLine.startsWith("⚠️") -> {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeaderBadge(title = "⚠️ Edge Cases & Traps", badgeColor = Color(0xFFF59E0B))
            val remainder = cleanLine.substringAfter("Traps").substringAfter("Cases").substringAfter("⚠️").removePrefix(":").removePrefix("**").trim()
            if (remainder.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = formatMarkdownInline(remainder),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 19.sp),
                modifier = Modifier.padding(vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
          lower.contains("guided socratic challenge") || lower.contains("guided question") || lower.contains("challenge") || cleanLine.startsWith("🧠") -> {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeaderBadge(title = "🧠 Guided Challenge", badgeColor = Color(0xFFA855F7))
            val remainder = cleanLine.substringAfter("Challenge").substringAfter("Question").substringAfter("🧠").removePrefix(":").removePrefix("**").trim()
            if (remainder.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = formatMarkdownInline(remainder),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 19.sp),
                modifier = Modifier.padding(vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
          cleanLine.startsWith("•") || cleanLine.startsWith("-") || cleanLine.startsWith("*") -> {
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("•", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
              Text(
                text = formatMarkdownInline(cleanLine.removePrefix("•").removePrefix("-").removePrefix("*").trim()),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 18.sp)
              )
            }
          }
          cleanLine.matches(Regex("""^\d+\..*""")) -> {
            val num = cleanLine.substringBefore(".")
            val rest = cleanLine.substringAfter(".").trim()
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("$num.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
              Text(
                text = formatMarkdownInline(rest),
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 18.sp)
              )
            }
          }
          cleanLine.isNotBlank() -> {
            Text(
              text = formatMarkdownInline(cleanLine),
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFE2E8F0),
                lineHeight = 18.sp
              ),
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
        }
        i++
      }
    }
  }
}

@Composable
private fun SectionHeaderBadge(title: String, badgeColor: Color) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = badgeColor.copy(alpha = 0.15f),
    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = badgeColor
      ),
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}
