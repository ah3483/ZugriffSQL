package com.fitnessapp.data.repository

import androidx.lifecycle.LiveData
import com.fitnessapp.data.dao.StatisticsDao
import com.fitnessapp.data.dao.WorkoutSessionDao
import com.fitnessapp.data.dao.WorkoutTemplateDao
import com.fitnessapp.data.model.*

class WorkoutRepository(
    private val templateDao: WorkoutTemplateDao,
    private val sessionDao: WorkoutSessionDao,
    private val statisticsDao: StatisticsDao
) {
    // Templates
    val allTemplates: LiveData<List<WorkoutTemplate>> = templateDao.getAllTemplates()

    suspend fun insertTemplate(template: WorkoutTemplate): Long =
        templateDao.insertTemplate(template)

    suspend fun updateTemplate(template: WorkoutTemplate) =
        templateDao.updateTemplate(template)

    suspend fun deleteTemplate(template: WorkoutTemplate) =
        templateDao.deleteTemplate(template)

    fun getTemplateWithExercises(templateId: Long): LiveData<WorkoutTemplateWithExercises?> =
        templateDao.getTemplateWithExercises(templateId)

    suspend fun getTemplateWithExercisesOnce(templateId: Long): WorkoutTemplateWithExercises? =
        templateDao.getTemplateWithExercisesOnce(templateId)

    // Exercises
    suspend fun insertExercise(exercise: ExerciseTemplate): Long =
        templateDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: ExerciseTemplate) =
        templateDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: ExerciseTemplate) =
        templateDao.deleteExercise(exercise)

    fun getExercisesForTemplate(templateId: Long): LiveData<List<ExerciseTemplate>> =
        templateDao.getExercisesForTemplate(templateId)

    // Sessions
    val allSessions: LiveData<List<WorkoutSession>> = sessionDao.getAllSessions()

    suspend fun insertSession(session: WorkoutSession): Long =
        sessionDao.insertSession(session)

    suspend fun updateSession(session: WorkoutSession) =
        sessionDao.updateSession(session)

    suspend fun deleteSession(session: WorkoutSession) =
        sessionDao.deleteSession(session)

    fun getSessionWithExercises(sessionId: Long): LiveData<WorkoutSessionWithExercises?> =
        sessionDao.getSessionWithExercises(sessionId)

    suspend fun getSessionWithExercisesOnce(sessionId: Long): WorkoutSessionWithExercises? =
        sessionDao.getSessionWithExercisesOnce(sessionId)

    suspend fun insertCompletedExercise(exercise: CompletedExercise): Long =
        sessionDao.insertCompletedExercise(exercise)

    suspend fun insertCompletedSet(set: CompletedSet): Long =
        sessionDao.insertCompletedSet(set)

    suspend fun insertCompletedSets(sets: List<CompletedSet>) =
        sessionDao.insertCompletedSets(sets)

    // Statistics
    val workoutSummary = statisticsDao.getWorkoutSummary()
    val personalRecords = statisticsDao.getPersonalRecords()
    val monthlyStats = statisticsDao.getMonthlyStats()
    val allExerciseNames = statisticsDao.getAllExerciseNames()
    val totalCompletedSessions = statisticsDao.getTotalCompletedSessions()
    val mostUsedTemplate = statisticsDao.getMostUsedTemplate()
    val totalVolumeKg = statisticsDao.getTotalVolumeKg()

    fun getExerciseProgress(exerciseName: String) =
        statisticsDao.getExerciseProgress(exerciseName)

    fun getSessionCountSince(since: Long) =
        statisticsDao.getSessionCountSince(since)

    fun searchSessions(query: String) =
        statisticsDao.searchSessions(query)
}
