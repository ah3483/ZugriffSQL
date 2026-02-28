package com.fitnessapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnessapp.data.model.*

@Dao
interface WorkoutSessionDao {

    @Insert
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): LiveData<List<WorkoutSession>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun getSessionWithExercises(sessionId: Long): LiveData<WorkoutSessionWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionWithExercisesOnce(sessionId: Long): WorkoutSessionWithExercises?

    @Insert
    suspend fun insertCompletedExercise(exercise: CompletedExercise): Long

    @Insert
    suspend fun insertCompletedSet(set: CompletedSet): Long

    @Insert
    suspend fun insertCompletedSets(sets: List<CompletedSet>)

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): LiveData<List<WorkoutSession>>
}
