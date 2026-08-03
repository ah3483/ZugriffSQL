package com.fitnessapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnessapp.data.model.ExerciseTemplate
import com.fitnessapp.data.model.WorkoutTemplate
import com.fitnessapp.data.model.WorkoutTemplateWithExercises

@Dao
interface WorkoutTemplateDao {

    @Insert
    suspend fun insertTemplate(template: WorkoutTemplate): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplate)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplate)

    @Query("SELECT * FROM workout_templates ORDER BY createdAt DESC")
    fun getAllTemplates(): LiveData<List<WorkoutTemplate>>

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    fun getTemplateWithExercises(templateId: Long): LiveData<WorkoutTemplateWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateWithExercisesOnce(templateId: Long): WorkoutTemplateWithExercises?

    @Insert
    suspend fun insertExercise(exercise: ExerciseTemplate): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseTemplate)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseTemplate)

    @Query("SELECT * FROM exercise_templates WHERE workoutTemplateId = :templateId ORDER BY orderIndex")
    fun getExercisesForTemplate(templateId: Long): LiveData<List<ExerciseTemplate>>

    @Query("DELETE FROM exercise_templates WHERE workoutTemplateId = :templateId")
    suspend fun deleteAllExercisesForTemplate(templateId: Long)

    @Transaction
    @Query("SELECT * FROM workout_templates ORDER BY createdAt DESC")
    suspend fun getAllTemplatesOnce(): List<WorkoutTemplateWithExercises>
}
