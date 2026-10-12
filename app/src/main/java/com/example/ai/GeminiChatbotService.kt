package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
  val id: String,
  val role: String, // "user" or "model"
  val text: String,
  val timestamp: Long = System.currentTimeMillis()
)

object GeminiChatbotService {

  private const val MODEL_NAME = "gemini-3.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private const val SYSTEM_INSTRUCTION = """
You are the AlgoVerse Socratic ADA Mentor, a specialized university professor for Analysis & Design of Algorithms (ADA).
Your mission: Help the student deeply master algorithm design techniques, recurrences, correctness proofs, and asymptotic bounds.

CRITICAL FORMATTING GUIDELINE:
You MUST ALWAYS structure your answers using the following 4 labeled sections:

🎯 Core Concept
- 1-2 concise, crystal-clear sentences defining the core algorithm invariant or principle.

💡 Step-by-Step Intuition
- Numbered or bulleted explanation of how the algorithm functions, recurrence relations (e.g., T(n) = 2T(n/2) + O(n)), or state transitions.
- Use concrete examples and Big-O notation.

⚠️ Edge Cases & Traps
- Common pitfalls, negative cycles, 0-inversions, base cases, or where greedy heuristics fail.

🧠 Guided Socratic Challenge
- Ask 1 thought-provoking question prompting the student to test their intuition on the next step.

Tone: Highly personalized to the student's chosen learning goal, encouraging, rigorous, and visually structured with clean markdown bullets and bold keywords.
"""

  suspend fun sendMessage(
    history: List<ChatMessage>,
    newPrompt: String,
    learningGoal: String = "Exam Preparation & Proofs",
    activeTopic: String = "Algorithms & Design"
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext getLocalSocraticResponse(newPrompt, learningGoal)
    }

    try {
      val contentsArray = JSONArray()

      // Add conversation history
      for (msg in history.takeLast(10)) {
        val partObj = JSONObject().put("text", msg.text)
        val contentObj = JSONObject()
          .put("role", if (msg.role == "user") "user" else "model")
          .put("parts", JSONArray().put(partObj))
        contentsArray.put(contentObj)
      }

      val personalizedPrompt = """
[Student Goal: $learningGoal | Active Topic: $activeTopic]
Question: $newPrompt
""".trimIndent()

      // Add new prompt
      contentsArray.put(
        JSONObject()
          .put("role", "user")
          .put("parts", JSONArray().put(JSONObject().put("text", personalizedPrompt)))
      )

      val systemInstructionObj = JSONObject()
        .put("parts", JSONArray().put(JSONObject().put("text", SYSTEM_INSTRUCTION)))

      val requestPayload = JSONObject()
        .put("contents", contentsArray)
        .put("systemInstruction", systemInstructionObj)
        .put(
          "generationConfig",
          JSONObject()
            .put("temperature", 0.7)
            .put("topP", 0.95)
            .put("topK", 40)
        )

      val requestBody = requestPayload.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url("$BASE_URL?key=$apiKey")
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        return@withContext getLocalSocraticResponse(newPrompt, learningGoal)
      }

      val json = JSONObject(responseString)
      val candidates = json.optJSONArray("candidates")
      val firstCandidate = candidates?.optJSONObject(0)
      val content = firstCandidate?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      val text = parts?.optJSONObject(0)?.optString("text")

      if (!text.isNullOrBlank()) {
        text.trim()
      } else {
        getLocalSocraticResponse(newPrompt, learningGoal)
      }
    } catch (e: Exception) {
      getLocalSocraticResponse(newPrompt, learningGoal)
    }
  }

  private fun getLocalSocraticResponse(prompt: String, learningGoal: String = "General"): String {
    val lower = prompt.lowercase()
    return when {
      "swap" in lower || "bubble" in lower -> {
        """
🎯 Core Concept
Inversion Theorem: In any array, every adjacent swap in Bubble Sort resolves exactly ONE inversion (a pair where i < j but arr[i] > arr[j]).

💡 Step-by-Step Intuition
• Total inversions I determine the minimum number of adjacent swaps required to sort the array.
• Worst Case (Reversed): n(n - 1) / 2 inversions → O(n²) comparisons and O(n²) swaps.
• Best Case (Sorted): 0 inversions → with an early exit flag, Bubble Sort detects 0 swaps in Pass 1 and halts in O(n) time!

⚠️ Edge Cases & Traps
• Without the early termination flag, Bubble Sort will blindly execute all n(n - 1) / 2 comparisons even on a sorted array!

🧠 Guided Socratic Challenge
Look at your current array in Topic 1. If you reverse the array, what happens to the live Inversion Counter after your very first swap?
""".trimIndent()
      }
      "dijkstra" in lower || "negative" in lower || "graph" in lower -> {
        """
🎯 Core Concept
Greedy Subpath Finality: Dijkstra's algorithm relies on the greedy property that when a vertex u is extracted from the min-priority queue (visited), its tentative distance dist[u] is optimal and permanently finalized.

💡 Step-by-Step Intuition
• At each step, extract vertex u with minimum dist[u].
• Relax all outgoing edges (u, v): if dist[u] + weight(u, v) < dist[v], update dist[v].
• Time Complexity: O((V + E) log V) using a min-heap priority queue.

⚠️ Edge Cases & Traps
• Negative Edge Weights: A negative edge reachable later can decrease the distance to an already 'visited' vertex, destroying Dijkstra's invariant!
• Negative Cycles: Paths become infinitely small (-∞), requiring Bellman-Ford (O(VE)).

🧠 Guided Socratic Challenge
In Topic 5, switch on 'Negative Edge Mode'. Why does node D's finalized distance become invalid when edge C→D has weight -5?
""".trimIndent()
      }
      "knapsack" in lower || "greedy" in lower || "dp" in lower -> {
        """
🎯 Core Concept
Optimal Substructure & No-Fractionality: In 0/1 Knapsack, items cannot be broken into fractions. Therefore, greedy selection by value-to-weight ratio fails because remaining capacity may go completely wasted.

💡 Step-by-Step Intuition
• Bellman Equation: dp[i][w] = max(dp[i-1][w], dp[i-1][w - wt[i]] + val[i]) if wt[i] ≤ w, else dp[i-1][w].
• Table State: Rows represent item choices 1..n, columns represent capacity 0..W.
• Time & Space: O(n · W) pseudo-polynomial dynamic programming.

⚠️ Edge Cases & Traps
• When capacity W is large (exponential in bit length), DP is pseudo-polynomial, not polynomial!
• Fractional Knapsack can be solved greedily in O(n log n), but 0/1 Knapsack is NP-Complete.

🧠 Guided Socratic Challenge
Check the live comparison in Topic 4: when capacity is W=50 and item weights are [10, 20, 30], why does DP yield +$60 more profit than Greedy?
""".trimIndent()
      }
      "recurrence" in lower || "merge" in lower || "master" in lower || "tree" in lower -> {
        """
🎯 Core Concept
Master Theorem & Divide-and-Conquer: Recurrences of form T(n) = aT(n/b) + f(n) characterize algorithms dividing problems into 'a' subproblems of size n/b with f(n) divide/combine work.

💡 Step-by-Step Intuition
• For Merge Sort: a = 2 subproblems, b = 2 factor, f(n) = O(n) merge work.
• Compare f(n) to n^{log_b(a)}: log_2(2) = 1 → n^1 = O(n).
• Case 2 applies: Since f(n) = Θ(n^{log_b(a)}), T(n) = Θ(n log n).

⚠️ Edge Cases & Traps
• Master Theorem cannot be directly applied if a < 1, b ≤ 1, or f(n) is not a polynomial ratio (e.g., T(n) = 2T(n/2) + n/log n).

🧠 Guided Socratic Challenge
If we divided Merge Sort into 3 subproblems of size n/3 instead of 2, what would the recurrence depth and total complexity be?
""".trimIndent()
      }
      "heap" in lower -> {
        """
🎯 Core Concept
Binary Max-Heap Invariant: For every node i, arr[i] ≥ arr[2i+1] (left child) and arr[i] ≥ arr[2i+2] (right child). The root arr[0] is always the maximum element.

💡 Step-by-Step Intuition
• Phase 1 (Build Heap): Bottom-up heapify from index n/2 - 1 down to 0 takes O(n) linear time!
• Phase 2 (Extraction): Swap root with last element arr[k], shrink heap boundary, and sift-down root in O(log n).
• Total Time: O(n log n) in all cases (Best, Average, Worst) with strictly O(1) auxiliary space!

⚠️ Edge Cases & Traps
• Heap Sort is NOT stable: Swapping distant nodes across the tree disturbs the original order of equal keys.

🧠 Guided Socratic Challenge
Why does building a heap from bottom-up take O(n) time rather than O(n log n)? Hint: Think of how many nodes are near the leaves!
""".trimIndent()
      }
      "queen" in lower || "prun" in lower || "backtrack" in lower -> {
        """
🎯 Core Concept
Backtracking & State-Space Pruning: The N-Queens problem searches an n-ary decision tree of queen placements. Rather than checking all nⁿ brute-force states, bounding functions evaluate row, column, and diagonal conflicts at each step to prune entire subtrees immediately.

💡 Step-by-Step Intuition
• Bounding Function: Two queens at (r1, c1) and (r2, c2) conflict if r1 == r2, c1 == c2, or |r1 - r2| == |c1 - c2| (diagonal).
• Depth-First Search: Place queen in row r at column c. If valid, recursively proceed to row r + 1. If no columns are valid, backtrack (remove queen at row r - 1).
• Search Space Reduction: For N=8, brute force checks 8⁸ ≈ 16.7 million states, while pruned backtracking evaluates only 2,057 nodes!

⚠️ Edge Cases & Traps
• No solution exists for N=2 and N=3. For N=1, the trivial 1x1 board is a valid solution. For N=4, exactly 2 distinct solutions exist.

🧠 Guided Socratic Challenge
Try placing queens manually in the Topic 6 Sandbox. If you place a queen at (0, 1) and (1, 3), how many cells in row 2 remain unthreatened?
""".trimIndent()
      }
      "bst" in lower || "binary search tree" in lower || "insert" in lower || "delete" in lower -> {
        """
🎯 Core Concept
BST Property & Invariant: For every node x with key k, all keys in x's left subtree are strictly < k, and all keys in x's right subtree are strictly > k. This invariant guarantees that an In-Order traversal yields sorted ascending order.

💡 Step-by-Step Intuition
• Insertion: Trace down from root comparing target v: if v < curr.key go left, else go right. Attach new node at the first null position. Time: O(h) where h is tree height.
• Deletion (3 Cases):
  1. Leaf node: Simply remove the pointer from parent.
  2. Single child: Replace node with its child pointer.
  3. Two children: Find in-order successor (minimum in right subtree), copy its value into current node, and recursively delete the successor.
• Search & Traversal: Search takes O(h). In-Order traversal visits Left → Root → Right in O(n) linear time.

⚠️ Edge Cases & Traps
• Skewed BST: Inserting sorted keys [10, 20, 30, 40] degenerates the tree into a linked list of height O(n), destroying logarithmic performance! Self-balancing trees (AVL, Red-Black) maintain h = O(log n).

🧠 Guided Socratic Challenge
In Topic 2, insert keys [50, 30, 70] and then delete root 50. Which node becomes the new root, and why?
""".trimIndent()
      }
      else -> {
        """
🎯 Core Concept
Personalized ADA Guidance ($learningGoal): AlgoVerse covers 8 foundational units: Sorting, Recursion Trees, DP Grids, Greedy Choice, Graph Relaxations, Tree Pruning, P vs NP, and Hand-Tracing.

💡 Step-by-Step Intuition
• Focus Area ($learningGoal): I can break down algorithm invariants, derive recurrences with the Master Theorem, or walk through step-by-step state transitions.
• Interactive Sandboxes: You can insert/delete custom nodes in the Graph (Topic 5), build your own BST (Topic 2), test N-Queens placements (Topic 6), or benchmark Heap Sort (Topic 1).

⚠️ Edge Cases & Traps
• Always verify boundary conditions (n = 0, n = 1, disconnected graph components, negative weights, and skewed trees).

🧠 Guided Socratic Challenge
Which algorithm topic would you like to explore? Try asking about Heap Sort heapify, Dijkstra negative edges, N-Queens pruning, or BST deletion!
""".trimIndent()
      }
    }
  }
}
