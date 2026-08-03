package com.fitnessapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

data class ExercisePersonalRecord(
    val exerciseName: String,
    val maxWeight: Double,
    val maxReps: Int,
    val totalSets: Int
)

data class WorkoutSummary(
    val totalSessions: Int,
    val totalExercises: Int,
    val totalSets: Int,
    val avgDurationMinutes: Double
)

data class MonthlyStats(
    val month: String,
    val sessionCount: Int,
    val totalSets: Int
)

data class ExerciseProgress(
    val date: Long,
    val maxWeight: Double,
    val maxReps: Int
)

@Dao
interface StatisticsDao {

    @Query("""
        SELECT COUNT(*) FROM workout_sessions
        WHERE completedAt IS NOT NULL
    """)
    fun getTotalCompletedSessions(): LiveData<Int>

    @Query("""
        SELECT
            COUNT(DISTINCT ws.id) as totalSessions,
            COUNT(DISTINCT ce.id) as totalExercises,
            COUNT(cs.id) as totalSets,
            COALESCE(AVG((ws.completedAt - ws.startedAt) / 60000.0), 0) as avgDurationMinutes
        FROM workout_sessions ws
        LEFT JOIN completed_exercises ce ON ce.sessionId = ws.id
        LEFT JOIN completed_sets cs ON cs.exerciseId = ce.id
        WHERE ws.completedAt IS NOT NULL
    """)
    fun getWorkoutSummary(): LiveData<WorkoutSummary>

    @Query("""
        SELECT
            ce.exerciseName,
            MAX(cs.weight) as maxWeight,
            MAX(cs.reps) as maxReps,
            COUNT(cs.id) as totalSets
        FROM completed_exercises ce
        JOIN completed_sets cs ON cs.exerciseId = ce.id
        WHERE cs.completed = 1
        GROUP BY ce.exerciseName
        ORDER BY ce.exerciseName
    """)
    fun getPersonalRecords(): LiveData<List<ExercisePersonalRecord>>

    @Query("""
        SELECT
            strftime('%Y-%m', ws.startedAt / 1000, 'unixepoch') as month,
            COUNT(DISTINCT ws.id) as sessionCount,
            COUNT(cs.id) as totalSets
        FROM workout_sessions ws
        LEFT JOIN completed_exercises ce ON ce.sessionId = ws.id
        LEFT JOIN completed_sets cs ON cs.exerciseId = ce.id
        WHERE ws.completedAt IS NOT NULL
        GROUP BY month
        ORDER BY month DESC
        LIMIT 12
    """)
    fun getMonthlyStats(): LiveData<List<MonthlyStats>>

    @Query("""
        SELECT DISTINCT ce.exerciseName
        FROM completed_exercises ce
        ORDER BY ce.exerciseName
    """)
    fun getAllExerciseNames(): LiveData<List<String>>

    @Query("""
        SELECT
            ws.startedAt as date,
            MAX(cs.weight) as maxWeight,
            MAX(cs.reps) as maxReps
        FROM completed_exercises ce
        JOIN completed_sets cs ON cs.exerciseId = ce.id
        JOIN workout_sessions ws ON ws.id = ce.sessionId
        WHERE ce.exerciseName = :exerciseName AND cs.completed = 1
        GROUP BY ws.id
        ORDER BY ws.startedAt
    """)
    fun getExerciseProgress(exerciseName: String): LiveData<List<ExerciseProgress>>

    @Query("""
        SELECT COUNT(*) FROM workout_sessions
        WHERE completedAt IS NOT NULL
        AND startedAt >= :since
    """)
    fun getSessionCountSince(since: Long): LiveData<Int>

    @Query("""
        SELECT ws.templateName
        FROM workout_sessions ws
        WHERE ws.completedAt IS NOT NULL
        GROUP BY ws.templateName
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    fun getMostUsedTemplate(): LiveData<String?>

    @Query("""
        SELECT COALESCE(SUM(cs.weight * cs.reps), 0)
        FROM completed_sets cs
        JOIN completed_exercises ce ON ce.id = cs.exerciseId
        JOIN workout_sessions ws ON ws.id = ce.sessionId
        WHERE cs.completed = 1 AND ws.completedAt IS NOT NULL
    """)
    fun getTotalVolumeKg(): LiveData<Double>

    @Query("""
        SELECT * FROM workout_sessions
        WHERE templateName LIKE '%' || :query || '%'
        ORDER BY startedAt DESC
    """)
    fun searchSessions(query: String): LiveData<List<com.fitnessapp.data.model.WorkoutSession>>
}
