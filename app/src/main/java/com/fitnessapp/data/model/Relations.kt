package com.fitnessapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutTemplateWithExercises(
    @Embedded val template: WorkoutTemplate,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutTemplateId"
    )
    val exercises: List<ExerciseTemplate>
)

data class WorkoutSessionWithExercises(
    @Embedded val session: WorkoutSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
        entity = CompletedExercise::class
    )
    val exercises: List<CompletedExerciseWithSets>
)

data class CompletedExerciseWithSets(
    @Embedded val exercise: CompletedExercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<CompletedSet>
)
