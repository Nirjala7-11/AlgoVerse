package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ComplexityClass(
  val name: String,
  val badgeColor: Color,
  val definition: String,
  val examples: List<String>
)

@Composable
fun Unit8TheoryScreen(
  onOpenSidebar: () -> Unit,
  onBackToDashboard: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  val classes = listOf(
    ComplexityClass(
      name = "P (Deterministic Polynomial Time)",
      badgeColor = Color(0xFF10B981),
      definition = "Problems solvable in O(n^k) polynomial time by a deterministic Turing machine.",
      examples = listOf("Bubble/Merge Sort O(n log n)", "Dijkstra's Shortest Path", "MST (Kruskal/Prim)", "Linear Programming")
    ),
    ComplexityClass(
      name = "NP (Nondeterministic Polynomial Time)",
      badgeColor = Color(0xFF3B82F6),
      definition = "Decision problems whose solutions can be verified in polynomial time given a certificate/witness.",
      examples = listOf("Hamiltonian Cycle", "Graph 3-Coloring", "Subset Sum", "All problems in P")
    ),
    ComplexityClass(
      name = "NP-Complete (The Hardest in NP)",
      badgeColor = Color(0xFFF59E0B),
      definition = "Problems in NP to which EVERY problem in NP can be reduced in polynomial time (Cook-Levin theorem).",
      examples = listOf("3-SAT (First proven NPC)", "0/1 Knapsack (Decision)", "Vertex Cover", "Travelling Salesperson (Decision)")
    ),
    ComplexityClass(
      name = "NP-Hard (At Least As Hard as NP)",
      badgeColor = Color(0xFFEF4444),
      definition = "Problems to which every problem in NP reduces, but need NOT be in NP themselves (can even be undecidable!).",
      examples = listOf("TSP (Optimization: find minimum tour)", "Halting Problem (Undecidable)", "Maximum Clique")
    )
  )

  var selectedClass by remember { mutableStateOf(classes[2]) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(14.dp)
  ) {
    // Header
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Dashboard",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Topic 7: Complexity Theory & P vs NP",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Polynomial Reductions & Classification Map",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Button(
          onClick = onOpenSidebar,
          modifier = Modifier.testTag("u8_sidebar_button")
        ) {
          Text("Topics")
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Interactive Venn-like Classification Hierarchy
    Text(
      text = "Interactive Complexity Classification Hierarchy:",
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(6.dp))

    classes.forEach { cls ->
      val isSelected = selectedClass == cls
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) cls.badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
          width = if (isSelected) 1.5.dp else 1.dp,
          color = if (isSelected) cls.badgeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { selectedClass = cls }
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(14.dp)
              .background(cls.badgeColor, RoundedCornerShape(4.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = cls.name,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) cls.badgeColor else MaterialTheme.colorScheme.onSurface
              )
            )
            Text(
              text = cls.definition,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Selected Class Inspector Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "Canonical Problem Examples in ${selectedClass.name}:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = selectedClass.badgeColor)
        )
        Spacer(modifier = Modifier.height(8.dp))

        selectedClass.examples.forEach { ex ->
          Row(
            modifier = Modifier.padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("•", color = selectedClass.badgeColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = ex,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Polynomial Reduction Flow Diagram Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = "Polynomial-Time Reduction Concept: A ≤_p B",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "\"Problem A reduces to Problem B in polynomial time\" means any instance of A can be transformed in polynomial time into an instance of B such that solving B also solves A.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Reduction Flow Diagram
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Instance of A", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("3-SAT Formula", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.padding(horizontal = 6.dp)
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Reduction f(x)", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B))
              Text("O(n^k) Transform", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.padding(horizontal = 6.dp)
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Instance of B", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Vertex Cover", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Core Takeaway: If B can be solved in polynomial time, then A can also be solved in polynomial time! Conversely, if A is known to be NP-Hard, then B must also be NP-Hard!",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        )
      }
    }
  }
}
