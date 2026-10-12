package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TopicCatalog
import com.example.model.TopicCategory
import com.example.model.TopicItem

@Composable
fun AlgoVerseSidebarContent(
  selectedTopicId: String,
  onSelectTopic: (String) -> Unit,
  onClose: () -> Unit,
  isDarkMode: Boolean = false,
  onToggleTheme: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Surface(
    modifier = modifier
      .width(320.dp)
      .fillMaxHeight(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(16.dp)
    ) {
      // Header with Logo and Close
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
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "AlgoVerse",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "ADA Syllabus Topics",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("close_sidebar_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Scrollable Topics List
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(scrollState)
      ) {
        // Dedicated Learning Hub Dashboard Button at Top
        val isDashboardSelected = selectedTopicId == "dashboard"
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isDashboardSelected) {
            MaterialTheme.colorScheme.primaryContainer
          } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          },
          border = if (isDashboardSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
          } else {
            null
          },
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              onSelectTopic("dashboard")
              onClose()
            }
            .testTag("sidebar_nav_dashboard")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isDashboardSelected) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              },
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Dashboard,
                  contentDescription = null,
                  tint = if (isDashboardSelected) {
                    MaterialTheme.colorScheme.onPrimary
                  } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                  },
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Learning Hub & Progress",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isDashboardSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                  } else {
                    MaterialTheme.colorScheme.onSurface
                  }
                )
              )
              Text(
                text = "Resume study • Mastery stats",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dedicated Algorithm Race & Comparison Mode Button
        val isCompareSelected = selectedTopicId == "compare_algorithms"
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isCompareSelected) {
            MaterialTheme.colorScheme.primaryContainer
          } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          },
          border = if (isCompareSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
          } else {
            null
          },
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              onSelectTopic("compare_algorithms")
              onClose()
            }
            .testTag("sidebar_nav_compare_algorithms")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isCompareSelected) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              },
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CompareArrows,
                  contentDescription = null,
                  tint = if (isCompareSelected) {
                    MaterialTheme.colorScheme.onPrimary
                  } else {
                    MaterialTheme.colorScheme.primary
                  },
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Algorithm Race (Compare)",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isCompareSelected) {
                      MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                      MaterialTheme.colorScheme.onSurface
                    }
                  )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                  Text(
                    text = "NEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Black,
                      color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "Side-by-side benchmark animation",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(10.dp))

        // Group topics by Topic Category (Topics 1-8)
        TopicCategory.entries.forEach { category ->
          CategoryGroup(
            category = category,
            selectedTopicId = selectedTopicId,
            onSelectTopic = { topicId ->
              onSelectTopic(topicId)
              onClose()
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Dark / Light Mode Switcher Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleTheme() }
          .testTag("sidebar_theme_toggle_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = if (isDarkMode) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f),
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                  contentDescription = null,
                  tint = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFFD97706),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (isDarkMode) "Dark Theme" else "Light Theme",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Tap to switch mode",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = isDarkMode,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = MaterialTheme.colorScheme.primary,
              checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
              uncheckedThumbColor = MaterialTheme.colorScheme.outline,
              uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.testTag("sidebar_theme_switch")
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryGroup(
  category: TopicCategory,
  selectedTopicId: String,
  onSelectTopic: (String) -> Unit
) {
  val categoryTopics = TopicCatalog.allTopics.filter { it.category == category }
  if (categoryTopics.isEmpty()) return

  val categoryIcon = when (category) {
    TopicCategory.TOPIC_1 -> Icons.Default.CompareArrows
    TopicCategory.TOPIC_2 -> Icons.Default.AccountTree
    TopicCategory.TOPIC_3 -> Icons.Default.GridOn
    TopicCategory.TOPIC_4 -> Icons.Default.ElectricBolt
    TopicCategory.TOPIC_5 -> Icons.Default.Hub
    TopicCategory.TOPIC_6 -> Icons.Default.Shield
    TopicCategory.TOPIC_7 -> Icons.Default.Psychology
    TopicCategory.TOPIC_8 -> Icons.Default.Quiz
  }

  // Category Header
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = categoryIcon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = category.title,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
  }

  // Topics inside category
  categoryTopics.forEach { topic ->
    val isSelected = selectedTopicId == topic.id

    Surface(
      shape = RoundedCornerShape(10.dp),
      color = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        Color.Transparent
      },
      border = if (isSelected) {
        androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
      } else {
        null
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)
        .clickable { onSelectTopic(topic.id) }
        .testTag("topic_nav_${topic.id}")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = topic.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
              } else {
                MaterialTheme.colorScheme.onSurface
              }
            )
          )
          Text(
            text = topic.description,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isSelected) {
            MaterialTheme.colorScheme.primary
          } else {
            MaterialTheme.colorScheme.surfaceVariant
          },
          modifier = Modifier.padding(start = 6.dp)
        ) {
          Text(
            text = topic.badge,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
              } else {
                MaterialTheme.colorScheme.primary
              }
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }

  Spacer(modifier = Modifier.height(8.dp))
}
