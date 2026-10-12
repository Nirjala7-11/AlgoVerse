package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class GraphNode(val id: String, val xRatio: Float, val yRatio: Float)
data class GraphEdge(val u: String, val v: String, val weight: Int, val isDirected: Boolean = false)

data class GraphAlgorithmStep(
  val stepIndex: Int,
  val currentNode: String,
  val activeEdge: Pair<String, String>?,
  val distances: Map<String, Int>,
  val visitedNodes: Set<String>,
  val queueOrStack: List<String>,
  val explanation: String
)

enum class GraphAlgorithm {
  DIJKSTRA, BFS, DFS
}

@Composable
fun Engine5GraphScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Dynamic Nodes and Edges State
  val nodes = remember {
    mutableStateListOf(
      GraphNode("A", 0.15f, 0.50f),
      GraphNode("B", 0.45f, 0.20f),
      GraphNode("C", 0.45f, 0.80f),
      GraphNode("D", 0.80f, 0.25f),
      GraphNode("E", 0.80f, 0.75f)
    )
  }

  val edges = remember {
    mutableStateListOf(
      GraphEdge("A", "B", 4),
      GraphEdge("A", "C", 2),
      GraphEdge("B", "C", 1),
      GraphEdge("B", "D", 5),
      GraphEdge("C", "D", 8),
      GraphEdge("C", "E", 10),
      GraphEdge("D", "E", 2)
    )
  }

  // Algorithm Control State
  var selectedAlgorithm by remember { mutableStateOf(GraphAlgorithm.DIJKSTRA) }
  var startNodeId by remember { mutableStateOf("A") }
  var currentStepIdx by remember { mutableIntStateOf(0) }
  var isPlaying by remember { mutableStateOf(false) }

  // Editor Input Fields
  var newNodeName by remember { mutableStateOf("") }
  var edgeSource by remember { mutableStateOf("") }
  var edgeDest by remember { mutableStateOf("") }
  var edgeWeightText by remember { mutableStateOf("3") }
  var isEdgeDirected by remember { mutableStateOf(false) }
  var editorErrorMessage by remember { mutableStateOf<String?>(null) }
  var showEditorCard by remember { mutableStateOf(true) }

  // Helper to re-arrange node positions aesthetically in a circular / layered layout
  fun recomputeNodePositions() {
    val count = nodes.size
    if (count == 0) return
    val centerX = 0.5f
    val centerY = 0.5f
    val radiusX = 0.35f
    val radiusY = 0.35f

    nodes.forEachIndexed { i, node ->
      val angle = (2 * Math.PI * i / count) - (Math.PI / 2)
      val x = (centerX + radiusX * cos(angle)).toFloat().coerceIn(0.12f, 0.88f)
      val y = (centerY + radiusY * sin(angle)).toFloat().coerceIn(0.15f, 0.85f)
      nodes[i] = node.copy(xRatio = x, yRatio = y)
    }
  }

  // Load Preset Graphs
  fun loadPreset(presetName: String) {
    isPlaying = false
    currentStepIdx = 0
    nodes.clear()
    edges.clear()
    when (presetName) {
      "standard" -> {
        nodes.addAll(
          listOf(
            GraphNode("A", 0.15f, 0.50f),
            GraphNode("B", 0.45f, 0.20f),
            GraphNode("C", 0.45f, 0.80f),
            GraphNode("D", 0.80f, 0.25f),
            GraphNode("E", 0.80f, 0.75f)
          )
        )
        edges.addAll(
          listOf(
            GraphEdge("A", "B", 4),
            GraphEdge("A", "C", 2),
            GraphEdge("B", "C", 1),
            GraphEdge("B", "D", 5),
            GraphEdge("C", "D", 8),
            GraphEdge("C", "E", 10),
            GraphEdge("D", "E", 2)
          )
        )
      }
      "negative" -> {
        nodes.addAll(
          listOf(
            GraphNode("A", 0.20f, 0.50f),
            GraphNode("B", 0.55f, 0.25f),
            GraphNode("C", 0.55f, 0.75f)
          )
        )
        edges.addAll(
          listOf(
            GraphEdge("A", "B", 5, true),
            GraphEdge("A", "C", 2, true),
            GraphEdge("C", "B", -6, true)
          )
        )
      }
      "cycle" -> {
        nodes.addAll(
          listOf(
            GraphNode("A", 0.25f, 0.25f),
            GraphNode("B", 0.75f, 0.25f),
            GraphNode("C", 0.75f, 0.75f),
            GraphNode("D", 0.25f, 0.75f)
          )
        )
        edges.addAll(
          listOf(
            GraphEdge("A", "B", 2, true),
            GraphEdge("B", "C", 4, true),
            GraphEdge("C", "D", 1, true),
            GraphEdge("D", "A", 3, true)
          )
        )
      }
    }
    if (nodes.none { it.id == startNodeId }) {
      startNodeId = nodes.firstOrNull()?.id ?: "A"
    }
  }

  // Dynamic Dijkstra Step Generator
  fun generateDijkstraSteps(): List<GraphAlgorithmStep> {
    if (nodes.isEmpty()) return emptyList()
    val steps = mutableListOf<GraphAlgorithmStep>()
    val nodeIds = nodes.map { it.id }.toSet()
    val start = if (startNodeId in nodeIds) startNodeId else nodes.first().id

    val distances = mutableMapOf<String, Int>()
    nodeIds.forEach { distances[it] = 999 }
    distances[start] = 0

    val visited = mutableSetOf<String>()
    steps.add(
      GraphAlgorithmStep(
        stepIndex = 0,
        currentNode = start,
        activeEdge = null,
        distances = distances.toMap(),
        visitedNodes = emptySet(),
        queueOrStack = listOf(start),
        explanation = "Initialized Dijkstra at source vertex $start with dist=0. All other vertices set to ∞."
      )
    )

    while (visited.size < nodeIds.size) {
      // Find unvisited vertex with smallest tentative distance
      val current = nodeIds.filter { it !in visited }.minByOrNull { distances[it] ?: 999 } ?: break
      val currentDist = distances[current] ?: 999
      if (currentDist >= 999) break // Remaining unreachable

      visited.add(current)

      // Find adjacent neighbors
      val neighbors = mutableListOf<Pair<String, Int>>()
      for (e in edges) {
        if (e.u == current) neighbors.add(Pair(e.v, e.weight))
        if (!e.isDirected && e.v == current) neighbors.add(Pair(e.u, e.weight))
      }

      var relaxedAny = false
      for ((v, weight) in neighbors) {
        val newDist = currentDist + weight
        val oldDist = distances[v] ?: 999

        if (newDist < oldDist) {
          distances[v] = newDist
          relaxedAny = true
          val isNegativeViolation = v in visited
          steps.add(
            GraphAlgorithmStep(
              stepIndex = steps.size,
              currentNode = current,
              activeEdge = Pair(current, v),
              distances = distances.toMap(),
              visitedNodes = visited.toSet(),
              queueOrStack = nodeIds.filter { it !in visited }.sortedBy { distances[it] ?: 999 },
              explanation = if (isNegativeViolation) {
                "⚠️ Negative Edge Detected! Edge $current→$v with weight $weight relaxed an ALREADY VISITED node $v from $oldDist to $newDist! Dijkstra's greedy invariant is violated!"
              } else {
                "Relaxing edge ($current, $v, weight $weight): dist($v) decreased from ${if (oldDist >= 999) "∞" else oldDist} to $newDist."
              }
            )
          )
        }
      }

      if (!relaxedAny && steps.size > 0) {
        steps.add(
          GraphAlgorithmStep(
            stepIndex = steps.size,
            currentNode = current,
            activeEdge = null,
            distances = distances.toMap(),
            visitedNodes = visited.toSet(),
            queueOrStack = nodeIds.filter { it !in visited }.sortedBy { distances[it] ?: 999 },
            explanation = "Visited $current with finalized shortest distance ${distances[current]}. No edges relaxed."
          )
        )
      }
    }

    steps.add(
      GraphAlgorithmStep(
        stepIndex = steps.size,
        currentNode = start,
        activeEdge = null,
        distances = distances.toMap(),
        visitedNodes = visited.toSet(),
        queueOrStack = emptyList(),
        explanation = "Dijkstra Execution Complete! Final shortest path distances from $start: ${distances.entries.joinToString { "${it.key}=${if (it.value >= 999) "∞" else it.value}" }}."
      )
    )

    return steps
  }

  // Dynamic BFS Step Generator
  fun generateBfsSteps(): List<GraphAlgorithmStep> {
    if (nodes.isEmpty()) return emptyList()
    val steps = mutableListOf<GraphAlgorithmStep>()
    val nodeIds = nodes.map { it.id }.toSet()
    val start = if (startNodeId in nodeIds) startNodeId else nodes.first().id

    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<String>()
    val distances = mutableMapOf<String, Int>()
    nodeIds.forEach { distances[it] = 999 }
    distances[start] = 0

    visited.add(start)
    queue.add(start)

    steps.add(
      GraphAlgorithmStep(
        stepIndex = 0,
        currentNode = start,
        activeEdge = null,
        distances = distances.toMap(),
        visitedNodes = setOf(start),
        queueOrStack = queue.toList(),
        explanation = "BFS initialized: Queued start node $start. Queue = [${queue.joinToString()}]."
      )
    )

    while (queue.isNotEmpty()) {
      val curr = queue.removeFirst()
      val currLevel = distances[curr] ?: 0

      // Get neighbors
      val neighbors = mutableListOf<String>()
      for (e in edges) {
        if (e.u == curr) neighbors.add(e.v)
        if (!e.isDirected && e.v == curr) neighbors.add(e.u)
      }

      for (nbr in neighbors) {
        if (nbr !in visited) {
          visited.add(nbr)
          distances[nbr] = currLevel + 1
          queue.add(nbr)

          steps.add(
            GraphAlgorithmStep(
              stepIndex = steps.size,
              currentNode = curr,
              activeEdge = Pair(curr, nbr),
              distances = distances.toMap(),
              visitedNodes = visited.toSet(),
              queueOrStack = queue.toList(),
              explanation = "BFS exploring ($curr -> $nbr): Discovered node $nbr at level ${currLevel + 1}. Added to Queue."
            )
          )
        }
      }
    }

    steps.add(
      GraphAlgorithmStep(
        stepIndex = steps.size,
        currentNode = start,
        activeEdge = null,
        distances = distances.toMap(),
        visitedNodes = visited.toSet(),
        queueOrStack = emptyList(),
        explanation = "BFS Complete! All reachable nodes visited in level-order from $start."
      )
    )

    return steps
  }

  // Dynamic DFS Step Generator
  fun generateDfsSteps(): List<GraphAlgorithmStep> {
    if (nodes.isEmpty()) return emptyList()
    val steps = mutableListOf<GraphAlgorithmStep>()
    val nodeIds = nodes.map { it.id }.toSet()
    val start = if (startNodeId in nodeIds) startNodeId else nodes.first().id

    val visited = mutableSetOf<String>()
    val stack = ArrayDeque<String>()
    val distances = mutableMapOf<String, Int>()
    nodeIds.forEach { distances[it] = 999 }
    distances[start] = 0

    fun dfs(node: String, depth: Int) {
      visited.add(node)
      distances[node] = depth
      stack.addLast(node)

      steps.add(
        GraphAlgorithmStep(
          stepIndex = steps.size,
          currentNode = node,
          activeEdge = null,
          distances = distances.toMap(),
          visitedNodes = visited.toSet(),
          queueOrStack = stack.toList(),
          explanation = "DFS visited $node at depth $depth. Call Stack = [${stack.joinToString(" -> ")}]."
        )
      )

      val neighbors = mutableListOf<String>()
      for (e in edges) {
        if (e.u == node) neighbors.add(e.v)
        if (!e.isDirected && e.v == node) neighbors.add(e.u)
      }

      for (nbr in neighbors) {
        if (nbr !in visited) {
          steps.add(
            GraphAlgorithmStep(
              stepIndex = steps.size,
              currentNode = node,
              activeEdge = Pair(node, nbr),
              distances = distances.toMap(),
              visitedNodes = visited.toSet(),
              queueOrStack = stack.toList(),
              explanation = "DFS traversing edge ($node -> $nbr) to explore deeper sub-branch."
            )
          )
          dfs(nbr, depth + 1)
        }
      }

      stack.removeLast()
    }

    dfs(start, 0)

    steps.add(
      GraphAlgorithmStep(
        stepIndex = steps.size,
        currentNode = start,
        activeEdge = null,
        distances = distances.toMap(),
        visitedNodes = visited.toSet(),
        queueOrStack = emptyList(),
        explanation = "DFS Complete! Backtracked to root $start."
      )
    )

    return steps
  }

  // Active steps based on selected algorithm
  val generatedSteps = remember(selectedAlgorithm, startNodeId, nodes.size, edges.size) {
    when (selectedAlgorithm) {
      GraphAlgorithm.DIJKSTRA -> generateDijkstraSteps()
      GraphAlgorithm.BFS -> generateBfsSteps()
      GraphAlgorithm.DFS -> generateDfsSteps()
    }
  }

  val currentStep = if (generatedSteps.isNotEmpty()) {
    generatedSteps[currentStepIdx.coerceIn(0, generatedSteps.size - 1)]
  } else {
    GraphAlgorithmStep(0, "A", null, emptyMap(), emptySet(), emptyList(), "Graph is empty. Insert a node above!")
  }

  // Playback Coroutine Loop
  LaunchedEffect(isPlaying, currentStepIdx, generatedSteps.size) {
    if (isPlaying) {
      if (currentStepIdx < generatedSteps.size - 1) {
        delay(600L)
        currentStepIdx++
      } else {
        isPlaying = false
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(14.dp)
  ) {
    // Header
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackToDashboard) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard", tint = MaterialTheme.colorScheme.primary)
          }
          Icon(Icons.Default.Hub, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Topic 5: Interactive Graph Studio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Insert/Delete Nodes & Edges • Dijkstra, BFS & DFS", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Button(onClick = onOpenSidebar, modifier = Modifier.testTag("e5_sidebar_button")) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Preset Buttons Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = false,
        onClick = { loadPreset("standard") },
        label = { Text("Standard 5-Node Graph") },
        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp)) }
      )
      FilterChip(
        selected = false,
        onClick = { loadPreset("negative") },
        label = { Text("Negative Edge (Dijkstra Trap!)") },
        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp)) }
      )
      FilterChip(
        selected = false,
        onClick = { loadPreset("cycle") },
        label = { Text("Directed 4-Cycle") },
        leadingIcon = { Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(14.dp)) }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Graph Editor & User Required Input (Insert/Delete Nodes & Edges)
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "✏️ Custom Graph Builder (Insert & Delete)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = if (showEditorCard) "Hide" else "Show",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .clickable { showEditorCard = !showEditorCard }
              .padding(4.dp)
          )
        }

        if (showEditorCard) {
          Spacer(modifier = Modifier.height(8.dp))

          // 1. Insert & Delete Node Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = newNodeName,
              onValueChange = { newNodeName = it.take(2).uppercase() },
              label = { Text("Node Name", fontSize = 11.sp) },
              placeholder = { Text("F") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )

            Button(
              onClick = {
                val name = newNodeName.trim()
                if (name.isNotBlank() && nodes.none { it.id == name }) {
                  nodes.add(GraphNode(name, 0.5f, 0.5f))
                  recomputeNodePositions()
                  newNodeName = ""
                  editorErrorMessage = null
                  currentStepIdx = 0
                } else {
                  editorErrorMessage = "Node already exists or invalid name."
                }
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Node", fontSize = 11.sp)
            }

            // Quick Delete Last Node
            IconButton(
              onClick = {
                if (nodes.isNotEmpty()) {
                  val last = nodes.removeLast()
                  edges.removeAll { it.u == last.id || it.v == last.id }
                  recomputeNodePositions()
                  if (startNodeId == last.id) {
                    startNodeId = nodes.firstOrNull()?.id ?: "A"
                  }
                  currentStepIdx = 0
                }
              },
              enabled = nodes.size > 2,
              modifier = Modifier.size(40.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Node", tint = Color(0xFFEF4444))
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 2. Insert Edge Inputs (u, v, weight, directed)
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedTextField(
              value = edgeSource,
              onValueChange = { edgeSource = it.take(2).uppercase() },
              label = { Text("From", fontSize = 10.sp) },
              placeholder = { Text("A") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
              value = edgeDest,
              onValueChange = { edgeDest = it.take(2).uppercase() },
              label = { Text("To", fontSize = 10.sp) },
              placeholder = { Text("B") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
              value = edgeWeightText,
              onValueChange = { edgeWeightText = it },
              label = { Text("Weight", fontSize = 10.sp) },
              placeholder = { Text("3") },
              modifier = Modifier.weight(1.2f),
              shape = RoundedCornerShape(8.dp)
            )

            Button(
              onClick = {
                val u = edgeSource.trim()
                val v = edgeDest.trim()
                val w = edgeWeightText.toIntOrNull()
                val nodeIds = nodes.map { it.id }.toSet()

                if (u in nodeIds && v in nodeIds && u != v && w != null) {
                  // Add or update edge
                  edges.removeAll { (it.u == u && it.v == v) || (!isEdgeDirected && it.u == v && it.v == u) }
                  edges.add(GraphEdge(u, v, w, isEdgeDirected))
                  editorErrorMessage = null
                  currentStepIdx = 0
                } else {
                  editorErrorMessage = "Ensure both nodes exist (and are different) with valid integer weight."
                }
              },
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Add Edge", fontSize = 11.sp)
            }
          }

          // Error note if invalid
          if (editorErrorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = editorErrorMessage ?: "",
              color = Color(0xFFEF4444),
              style = MaterialTheme.typography.labelSmall
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Edge Options Toggles
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              FilterChip(
                selected = isEdgeDirected,
                onClick = { isEdgeDirected = !isEdgeDirected },
                label = { Text(if (isEdgeDirected) "Directed Edge (u ➔ v)" else "Undirected Edge (u ── v)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
              )
            }

            Text(
              text = "${nodes.size} Nodes • ${edges.size} Edges",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Algorithm & Source Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(
          selected = selectedAlgorithm == GraphAlgorithm.DIJKSTRA,
          onClick = { selectedAlgorithm = GraphAlgorithm.DIJKSTRA; currentStepIdx = 0 },
          label = { Text("Dijkstra") }
        )
        FilterChip(
          selected = selectedAlgorithm == GraphAlgorithm.BFS,
          onClick = { selectedAlgorithm = GraphAlgorithm.BFS; currentStepIdx = 0 },
          label = { Text("BFS") }
        )
        FilterChip(
          selected = selectedAlgorithm == GraphAlgorithm.DFS,
          onClick = { selectedAlgorithm = GraphAlgorithm.DFS; currentStepIdx = 0 },
          label = { Text("DFS") }
        )
      }

      // Start Node Picker
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Start: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        nodes.forEach { n ->
          val isSelected = n.id == startNodeId
          Box(
            modifier = Modifier
              .padding(horizontal = 2.dp)
              .size(26.dp)
              .clip(CircleShape)
              .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
              .clickable { startNodeId = n.id; currentStepIdx = 0 },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = n.id,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Step Explanation Banner
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFF1E293B),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = Color(0xFF38BDF8),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = currentStep.explanation,
          style = MaterialTheme.typography.bodySmall.copy(color = Color.White, lineHeight = 18.sp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Graph Canvas
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
      modifier = Modifier
        .fillMaxWidth()
        .height(290.dp)
        .testTag("graph_canvas")
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val nodePositions = nodes.associate { it.id to Offset(it.xRatio * w, it.yRatio * h) }

          // 1. Draw Edges
          for (edge in edges) {
            val p1 = nodePositions[edge.u]
            val p2 = nodePositions[edge.v]
            if (p1 != null && p2 != null) {
              val isActiveEdge = currentStep.activeEdge?.let {
                (it.first == edge.u && it.second == edge.v) || (!edge.isDirected && it.first == edge.v && it.second == edge.u)
              } == true

              val isNegative = edge.weight < 0

              drawLine(
                color = when {
                  isActiveEdge -> Color(0xFFF59E0B)
                  isNegative -> Color(0xFFEF4444)
                  else -> Color(0xFF334155)
                },
                start = p1,
                end = p2,
                strokeWidth = if (isActiveEdge) 3.5.dp.toPx() else 1.8.dp.toPx()
              )

              // Weight label on edge
              val mid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f - 6.dp.toPx())
              val weightPaint = android.graphics.Paint().apply {
                color = when {
                  isActiveEdge -> android.graphics.Color.parseColor("#F59E0B")
                  isNegative -> android.graphics.Color.parseColor("#EF4444")
                  else -> android.graphics.Color.parseColor("#94A3B8")
                }
                textSize = 22f
                isFakeBoldText = true
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
              }
              drawContext.canvas.nativeCanvas.drawText("${edge.weight}", mid.x, mid.y, weightPaint)
            }
          }

          // 2. Draw Nodes
          for (node in nodes) {
            val pos = nodePositions[node.id] ?: continue
            val isCurrent = node.id == currentStep.currentNode
            val isVisited = currentStep.visitedNodes.contains(node.id)

            val nodeBg = when {
              isCurrent -> Color(0xFFF59E0B) // Amber
              isVisited -> Color(0xFF10B981) // Green
              else -> Color(0xFF0284C7) // Sky blue
            }

            drawCircle(color = nodeBg, radius = 22.dp.toPx(), center = pos)
            drawCircle(color = Color(0xFF0F172A), radius = 17.dp.toPx(), center = pos)

            // Node Name
            val namePaint = android.graphics.Paint().apply {
              color = android.graphics.Color.WHITE
              textSize = 22f
              textAlign = android.graphics.Paint.Align.CENTER
              isFakeBoldText = true
              isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(node.id, pos.x, pos.y + 7.dp.toPx(), namePaint)

            // Live Distance/Depth Label below node
            val dist = currentStep.distances[node.id] ?: 999
            val labelText = if (selectedAlgorithm == GraphAlgorithm.DIJKSTRA) {
              if (dist >= 999) "d=∞" else "d=$dist"
            } else {
              if (dist >= 999) "—" else "lvl=$dist"
            }

            val distPaint = android.graphics.Paint().apply {
              color = if (isVisited) android.graphics.Color.parseColor("#34D399") else android.graphics.Color.parseColor("#38BDF8")
              textSize = 18f
              textAlign = android.graphics.Paint.Align.CENTER
              isFakeBoldText = true
              isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(labelText, pos.x, pos.y + 36.dp.toPx(), distPaint)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Live Metrics & Queue/Stack Inspector Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = when (selectedAlgorithm) {
              GraphAlgorithm.DIJKSTRA -> "Dijkstra Tentative Distances"
              GraphAlgorithm.BFS -> "BFS Queue Structure"
              GraphAlgorithm.DFS -> "DFS Call Stack"
            },
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
          )
          Text(
            text = "Step ${currentStepIdx + 1} / ${generatedSteps.size.coerceAtLeast(1)}",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Distance Pills
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          nodes.forEach { node ->
            val dist = currentStep.distances[node.id] ?: 999
            val isVisited = currentStep.visitedNodes.contains(node.id)
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isVisited) Color(0xFF065F46) else Color(0xFF0F172A),
              modifier = Modifier.padding(2.dp)
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(node.id, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                Text(
                  text = if (dist >= 999) "∞" else "$dist",
                  style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = Color(0xFF38BDF8))
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Playback Controls
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { currentStepIdx = 0; isPlaying = false }) {
          Icon(Icons.Default.Replay, contentDescription = "Restart")
        }
        IconButton(onClick = { if (currentStepIdx > 0) currentStepIdx--; isPlaying = false }, enabled = currentStepIdx > 0) {
          Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
        }
        FilledIconButton(
          onClick = {
            if (currentStepIdx >= generatedSteps.size - 1) {
              currentStepIdx = 0
              isPlaying = true
            } else {
              isPlaying = !isPlaying
            }
          }
        ) {
          Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play/Pause")
        }
        IconButton(
          onClick = {
            if (currentStepIdx < generatedSteps.size - 1) currentStepIdx++
            isPlaying = false
          },
          enabled = currentStepIdx < generatedSteps.size - 1
        ) {
          Icon(Icons.Default.SkipNext, contentDescription = "Next")
        }
        IconButton(onClick = { currentStepIdx = generatedSteps.size - 1; isPlaying = false }) {
          Icon(Icons.Default.FastForward, contentDescription = "End")
        }
      }
    }
  }
}
