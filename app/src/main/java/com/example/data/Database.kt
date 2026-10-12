package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "student_mastery")
data class StudentMastery(
  @PrimaryKey val algorithmKey: String, // "BUBBLE_SORT", "SELECTION_SORT", etc.
  val totalPredictions: Int = 0,
  val correctPredictions: Int = 0,
  val currentStreak: Int = 0,
  val bestStreak: Int = 0,
  val arraysSortedCount: Int = 0,
  val lastPracticedTimestamp: Long = System.currentTimeMillis(),
  val quizScorePercent: Int = 0,
  val quizCompletedCount: Int = 0,
  val suggestedNextTopicId: String = "merge_sort_tree"
)

@Dao
interface MasteryDao {
  @Query("SELECT * FROM student_mastery WHERE algorithmKey = :key")
  suspend fun getMastery(key: String): StudentMastery?

  @Query("SELECT * FROM student_mastery")
  fun getAllMastery(): Flow<List<StudentMastery>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertMastery(mastery: StudentMastery)
}

@Database(entities = [StudentMastery::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun masteryDao(): MasteryDao
}
