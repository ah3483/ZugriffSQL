package com.fitnessapp

import android.app.Application
import com.fitnessapp.data.FitnessDatabase
import com.fitnessapp.data.repository.WorkoutRepository

class FitnessApp : Application() {

    val database by lazy { FitnessDatabase.getDatabase(this) }
    val repository by lazy {
        WorkoutRepository(
            database.workoutTemplateDao(),
            database.workoutSessionDao()
        )
    }
}
