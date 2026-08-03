package com.fitnessapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitnessapp.data.dao.StatisticsDao
import com.fitnessapp.data.dao.WorkoutSessionDao
import com.fitnessapp.data.dao.WorkoutTemplateDao
import com.fitnessapp.data.model.*

@Database(
    entities = [
        WorkoutTemplate::class,
        ExerciseTemplate::class,
        WorkoutSession::class,
        CompletedExercise::class,
        CompletedSet::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun statisticsDao(): StatisticsDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
