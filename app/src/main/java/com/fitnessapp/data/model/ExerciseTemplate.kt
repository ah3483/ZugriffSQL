package com.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_templates",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplate::class,
            parentColumns = ["id"],
            childColumns = ["workoutTemplateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutTemplateId")]
)
data class ExerciseTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutTemplateId: Long,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Double = 0.0,
    val orderIndex: Int = 0
)
