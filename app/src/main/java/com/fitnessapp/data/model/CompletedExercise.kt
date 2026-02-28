package com.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "completed_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class CompletedExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val orderIndex: Int = 0
)
