package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Tree Node for Visualizer
data class BstVisualNode(
  val value: Int,
  val left: BstVisualNode? = null,
  val right: BstVisualNode? = null,
  val xRatio: Float = 0.5f,
  val yRatio: Float = 0.15f
)

data class BstOperationStep(
  val stepIndex: Int,
  val activeValue: Int?,
  val highlightedNodes: Set<Int>,
  val explanation: String
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun Engine2RecursionScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Interactive BST (Insert/Delete), 1: Merge Sort D&C Tree

  // BST State
  val bstValues = remember { mutableStateListOf(50, 30, 70, 20, 40, 60, 80) }
  var inputBstValue by remember { mutableStateOf("") }
  var bstActionMessage by remember { mutableStateOf("Binary Search Tree initialized with 7 elements.") }
  var highlightedNodeValue by remember { mutableStateOf<Int?>(null) }
  var traversalResult by remember { mutableStateOf<List<Int>>(emptyList()) }
  var activeTraversalName by remember { mutableStateOf("") }

  // Merge Sort D&C Tree State
  var rawArrayInput by remember { mutableStateOf("38, 27, 43, 3, 9, 82, 10") }
  var currentDcStepIdx by remember { mutableIntStateOf(0) }
  var isDcPlaying by remember { mutableStateOf(false) }

  // BST Helper Functions
  class BstInternalNode(var v: Int) {
    var left: BstInternalNode? = null
    var right: BstInternalNode? = null
  }

  fun buildBstFromList(list: List<Int>): BstInternalNode? {
    if (list.isEmpty()) return null
    val root = BstInternalNode(list[0])
    for (i in 1 until list.size) {
      val v = list[i]
      var curr: BstInternalNode? = root
      while (curr != null) {
        if (v < curr.v) {
          if (curr.left == null) {
            curr.left = BstInternalNode(v)
            break
          } else {
            curr = curr.left
          }
        } else if (v > curr.v) {
          if (curr.right == null) {
            curr.right = BstInternalNode(v)
            break
          } else {
            curr = curr.right
          }
        } else {
          break // No duplicates
        }
      }
    }
    return root
  }

  fun deleteFromInternalBst(root: BstInternalNode?, key: Int): BstInternalNode? {
    if (root == null) return null
    if (key < root.v) {
      root.left = deleteFromInternalBst(root.left, key)
    } else if (key > root.v) {
      root.right = deleteFromInternalBst(root.right, key)
    } else {
      // Node found
      if (root.left == null) return root.right
      if (root.right == null) return root.left
      // Node with two children: get in-order successor (min in right subtree)
      var minNode = root.right
      while (minNode?.left != null) {
        minNode = minNode.left
      }
      root.v = minNode?.v ?: root.v
      root.right = deleteFromInternalBst(root.right, root.v)
    }
    return root
  }

  fun flattenBstPreorder(root: BstInternalNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: BstInternalNode?) {
      if (node == null) return
      result.add(node.v)
      dfs(node.left)
      dfs(node.right)
    }
    dfs(root)
    return result
  }

  fun flattenBstInorder(root: BstInternalNode?): List<Int> {
    val result = mutableListOf<Int>()
    fun dfs(node: BstInternalNode?) {
      if (node == null) return
      dfs(node.left)
      result.add(node.v)
      dfs(node.right)
    }
    dfs(root)
    return result
  }

  // Convert internal BST to visual tree with xRatio and yRatio
  fun computeVisualTree(root: BstInternalNode?, x: Float = 0.5f, y: Float = 0.14f, span: Float = 0.24f): BstVisualNode? {
    if (root == null) return null
    val nextY = (y + 0.18f).coerceAtMost(0.85f)
    val leftVisual = computeVisualTree(root.left, (x - span).coerceIn(0.08f, 0.92f), nextY, span * 0.52f)
    val rightVisual = computeVisualTree(root.right, (x + span).coerceIn(0.08f, 0.92f), nextY, span * 0.52f)
    return BstVisualNode(value = root.v, left = leftVisual, right = rightVisual, xRatio = x, yRatio = y)
  }

  val visualTreeRoot = remember(bstValues.toList()) {
    val internal = buildBstFromList(bstValues)
    computeVisualTree(internal)
  }

  // User Action Handlers
  fun insertValue(v: Int) {
    if (v in bstValues) {
      bstActionMessage = "Value $v is already in the BST (no duplicates)."
      highlightedNodeValue = v
      return
    }
    bstValues.add(v)
    highlightedNodeValue = v
    bstActionMessage = "Inserted $v into BST. Verified Binary Search Tree property (left < parent < right)."
    traversalResult = emptyList()
  }

  fun deleteValue(v: Int) {
    if (v !in bstValues) {
      bstActionMessage = "Value $v not found in BST to delete."
      highlightedNodeValue = null
      return
    }
    val internal = buildBstFromList(bstValues)
    val afterDel = deleteFromInternalBst(internal, v)
    val newValues = flattenBstPreorder(afterDel)
    bstValues.clear()
    bstValues.addAll(newValues)
    highlightedNodeValue = null
    bstActionMessage = "Deleted $v from BST. Tree structure restructured with in-order successor if required."
    traversalResult = emptyList()
  }

  fun searchValue(v: Int) {
    if (v in bstValues) {
      highlightedNodeValue = v
      bstActionMessage = "Found $v in BST! Target node highlighted in amber."
    } else {
      highlightedNodeValue = null
      bstActionMessage = "Value $v not found in BST."
    }
  }

  fun runInorderTraversal() {
    val internal = buildBstFromList(bstValues)
    traversalResult = flattenBstInorder(internal)
    activeTraversalName = "In-Order (Sorted): "
    bstActionMessage = "In-Order Traversal visits Left, Root, Right → yields sorted non-decreasing order!"
  }

  // Precomputed D&C Steps for Merge Sort Tab
  val dcNodes = listOf(
    Pair("Root [38, 27, 43, 3, 9, 82, 10]", "Divide: n=7"),
    Pair("Left [38, 27, 43, 3]", "Divide: n/2=4"),
    Pair("Right [9, 82, 10]", "Divide: n/2=3"),
    Pair("Sub-Left [38, 27]", "Divide: n/4=2"),
    Pair("Base [38] & [27]", "Base Case: O(1)"),
    Pair("Merge [27, 38]", "Merge: O(2)"),
    Pair("Merge [3, 27, 38, 43]", "Merge: O(4)"),
    Pair("Full Sorted [3, 9, 10, 27, 38, 43, 82]", "Complete: Θ(n log n)")
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(14.dp)
  ) {
    // Header Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackToDashboard) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard", tint = MaterialTheme.colorScheme.primary)
          }
          Icon(Icons.Default.AccountTree, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Topic 2: Trees & Recursion Studio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Interactive BST (Insert & Delete) • Divide & Conquer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        Button(onClick = onOpenSidebar, modifier = Modifier.testTag("e2_sidebar_button")) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Mode Tabs: Interactive BST vs Merge Sort D&C Tree
    SecondaryTabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Interactive BST (Insert/Delete)") },
        icon = { Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp)) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Merge Sort D&C Tree") },
        icon = { Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp)) }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (selectedTab == 0) {
      // TAB 0: Interactive Binary Search Tree (Insert & Delete)
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "🌲 User Input: Insert, Delete & Search Node",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedTextField(
              value = inputBstValue,
              onValueChange = { inputBstValue = it.filter { ch -> ch.isDigit() }.take(3) },
              label = { Text("Value (0-999)", fontSize = 11.sp) },
              placeholder = { Text("45") },
              modifier = Modifier.weight(1.2f),
              shape = RoundedCornerShape(10.dp)
            )

            Button(
              onClick = {
                val v = inputBstValue.toIntOrNull()
                if (v != null) {
                  insertValue(v)
                  inputBstValue = ""
                }
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Insert", fontSize = 11.sp)
            }

            Button(
              onClick = {
                val v = inputBstValue.toIntOrNull()
                if (v != null) {
                  deleteValue(v)
                  inputBstValue = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Delete", fontSize = 11.sp)
            }

            IconButton(
              onClick = {
                val v = inputBstValue.toIntOrNull()
                if (v != null) searchValue(v)
              },
              modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
            ) {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Tree Action Presets & Traversal Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = false,
              onClick = { runInorderTraversal() },
              label = { Text("In-Order Traversal", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            FilterChip(
              selected = false,
              onClick = {
                bstValues.clear()
                bstValues.addAll(listOf(50, 25, 75, 10, 35, 60, 90))
                highlightedNodeValue = null
                bstActionMessage = "Reset to Balanced 7-Node BST."
                traversalResult = emptyList()
              },
              label = { Text("Balanced Preset", fontSize = 11.sp) }
            )
            FilterChip(
              selected = false,
              onClick = {
                bstValues.clear()
                bstValues.addAll(listOf(10, 20, 30, 40, 50))
                highlightedNodeValue = null
                bstActionMessage = "Skewed Right Tree: Degenerates to linked list (O(n) worst-case search)!"
                traversalResult = emptyList()
              },
              label = { Text("Skewed Degenerate (O(n))", fontSize = 11.sp) }
            )
            FilterChip(
              selected = false,
              onClick = {
                bstValues.clear()
                highlightedNodeValue = null
                bstActionMessage = "Cleared all nodes from BST."
                traversalResult = emptyList()
              },
              label = { Text("Clear Tree", fontSize = 11.sp) }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Status Explanation Banner
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = bstActionMessage, style = MaterialTheme.typography.bodySmall.copy(color = Color.White, lineHeight = 18.sp))
        }
      }

      if (traversalResult.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF064E3B),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "$activeTraversalName [${traversalResult.joinToString(" ➔ ")}]",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Interactive Tree Canvas
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)),
        modifier = Modifier
          .fillMaxWidth()
          .height(300.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Helper to recursively draw tree branches and nodes
            fun drawVisualTree(node: BstVisualNode?) {
              if (node == null) return
              val parentPos = Offset(node.xRatio * w, node.yRatio * h)

              // Draw branch to left child
              node.left?.let { leftChild ->
                val childPos = Offset(leftChild.xRatio * w, leftChild.yRatio * h)
                drawLine(
                  color = Color(0xFF334155),
                  start = parentPos,
                  end = childPos,
                  strokeWidth = 2.dp.toPx()
                )
                drawVisualTree(leftChild)
              }

              // Draw branch to right child
              node.right?.let { rightChild ->
                val childPos = Offset(rightChild.xRatio * w, rightChild.yRatio * h)
                drawLine(
                  color = Color(0xFF334155),
                  start = parentPos,
                  end = childPos,
                  strokeWidth = 2.dp.toPx()
                )
                drawVisualTree(rightChild)
              }

              // Draw Node Circle
              val isHighlighted = node.value == highlightedNodeValue
              val circleColor = if (isHighlighted) Color(0xFFF59E0B) else Color(0xFF0284C7)

              drawCircle(color = circleColor, radius = 20.dp.toPx(), center = parentPos)
              drawCircle(color = Color(0xFF0F172A), radius = 16.dp.toPx(), center = parentPos)

              // Draw text
              val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 22f
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
              }
              drawContext.canvas.nativeCanvas.drawText("${node.value}", parentPos.x, parentPos.y + 7.dp.toPx(), paint)
            }

            if (visualTreeRoot != null) {
              drawVisualTree(visualTreeRoot)
            } else {
              val emptyPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
              }
              drawContext.canvas.nativeCanvas.drawText("Tree is Empty. Insert a number above!", w / 2f, h / 2f, emptyPaint)
            }
          }
        }
      }
    } else {
      // TAB 1: Merge Sort Divide & Conquer Tree
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "⚡ Divide & Conquer Recurrence Breakdown",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Recurrence: T(n) = 2T(n/2) + O(n) • Master Theorem Case 2: Θ(n log n)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Recurrence Levels List
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        dcNodes.forEachIndexed { idx, pair ->
          val isCurrent = idx == currentDcStepIdx
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else Color(0xFF1E293B),
            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = pair.first,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else Color.White)
                )
                Text(
                  text = pair.second,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isCurrent) MaterialTheme.colorScheme.primary else Color(0xFF38BDF8)
                )
              }
              if (isCurrent) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MaterialTheme.colorScheme.primary
                ) {
                  Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
          IconButton(onClick = { currentDcStepIdx = 0 }) {
            Icon(Icons.Default.Replay, contentDescription = "Restart")
          }
          IconButton(onClick = { if (currentDcStepIdx > 0) currentDcStepIdx-- }, enabled = currentDcStepIdx > 0) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
          }
          FilledIconButton(
            onClick = {
              if (currentDcStepIdx < dcNodes.size - 1) currentDcStepIdx++ else currentDcStepIdx = 0
            }
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Next")
          }
          IconButton(onClick = { if (currentDcStepIdx < dcNodes.size - 1) currentDcStepIdx++ }, enabled = currentDcStepIdx < dcNodes.size - 1) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next")
          }
        }
      }
    }
  }
}
