package com.fitnessapp.ui.workout

import androidx.lifecycle.*
import com.fitnessapp.data.model.*
import com.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

data class ActiveExercise(
    val name: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val completedSets: MutableList<SetResult> = mutableListOf()
)

data class SetResult(
    var reps: Int,
    var weight: Double,
    var completed: Boolean = false
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _activeExercises = MutableLiveData<List<ActiveExercise>>(emptyList())
    val activeExercises: LiveData<List<ActiveExercise>> = _activeExercises

    private val _workoutFinished = MutableLiveData(false)
    val workoutFinished: LiveData<Boolean> = _workoutFinished

    private var templateName: String = ""
    private var templateId: Long? = null
    private var sessionStartTime: Long = 0

    fun startWorkoutFromTemplate(tplId: Long) {
        viewModelScope.launch {
            val templateWithExercises = repository.getTemplateWithExercisesOnce(tplId) ?: return@launch
            templateName = templateWithExercises.template.name
            templateId = tplId
            sessionStartTime = System.currentTimeMillis()

            val exercises = templateWithExercises.exercises.sortedBy { it.orderIndex }.map { ex ->
                ActiveExercise(
                    name = ex.name,
                    targetSets = ex.sets,
                    targetReps = ex.reps,
                    targetWeight = ex.weight,
                    completedSets = (1..ex.sets).map {
                        SetResult(reps = ex.reps, weight = ex.weight)
                    }.toMutableList()
                )
            }
            _activeExercises.value = exercises
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        val exercises = _activeExercises.value?.toMutableList() ?: return
        if (exerciseIndex < exercises.size) {
            val exercise = exercises[exerciseIndex]
            if (setIndex < exercise.completedSets.size) {
                exercise.completedSets[setIndex].completed = !exercise.completedSets[setIndex].completed
                _activeExercises.value = exercises
            }
        }
    }

    fun updateSetValues(exerciseIndex: Int, setIndex: Int, reps: Int, weight: Double) {
        val exercises = _activeExercises.value?.toMutableList() ?: return
        if (exerciseIndex < exercises.size) {
            val exercise = exercises[exerciseIndex]
            if (setIndex < exercise.completedSets.size) {
                exercise.completedSets[setIndex].reps = reps
                exercise.completedSets[setIndex].weight = weight
                _activeExercises.value = exercises
            }
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            val exercises = _activeExercises.value ?: return@launch

            val sessionId = repository.insertSession(
                WorkoutSession(
                    templateId = templateId,
                    templateName = templateName,
                    startedAt = sessionStartTime,
                    completedAt = System.currentTimeMillis()
                )
            )

            exercises.forEachIndexed { exIndex, exercise ->
                val completedExId = repository.insertCompletedExercise(
                    CompletedExercise(
                        sessionId = sessionId,
                        exerciseName = exercise.name,
                        orderIndex = exIndex
                    )
                )

                val sets = exercise.completedSets.mapIndexed { setIdx, setResult ->
                    CompletedSet(
                        exerciseId = completedExId,
                        setNumber = setIdx + 1,
                        reps = setResult.reps,
                        weight = setResult.weight,
                        completed = setResult.completed
                    )
                }
                repository.insertCompletedSets(sets)
            }

            _workoutFinished.value = true
        }
    }
}

class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
