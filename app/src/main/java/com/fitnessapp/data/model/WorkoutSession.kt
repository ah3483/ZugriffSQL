package com.fitnessapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("templateId")]
)
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long? = null,
    val templateName: String,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val notes: String = ""
)
