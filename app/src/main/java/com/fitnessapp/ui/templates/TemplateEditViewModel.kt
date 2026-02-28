package com.fitnessapp.ui.templates

import androidx.lifecycle.*
import com.fitnessapp.data.model.ExerciseTemplate
import com.fitnessapp.data.model.WorkoutTemplate
import com.fitnessapp.data.model.WorkoutTemplateWithExercises
import com.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class TemplateEditViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _templateId = MutableLiveData<Long>()

    val templateWithExercises: LiveData<WorkoutTemplateWithExercises?> = _templateId.switchMap { id ->
        if (id > 0) repository.getTemplateWithExercises(id)
        else MutableLiveData(null)
    }

    val exercises: LiveData<List<ExerciseTemplate>> = _templateId.switchMap { id ->
        if (id > 0) repository.getExercisesForTemplate(id)
        else MutableLiveData(emptyList())
    }

    private val _savedTemplateId = MutableLiveData<Long>()
    val savedTemplateId: LiveData<Long> = _savedTemplateId

    fun loadTemplate(templateId: Long) {
        _templateId.value = templateId
    }

    fun saveTemplate(name: String, description: String) {
        viewModelScope.launch {
            val currentId = _templateId.value ?: 0
            if (currentId > 0) {
                val template = templateWithExercises.value?.template
                if (template != null) {
                    repository.updateTemplate(template.copy(name = name, description = description))
                    _savedTemplateId.value = currentId
                }
            } else {
                val id = repository.insertTemplate(WorkoutTemplate(name = name, description = description))
                _templateId.value = id
                _savedTemplateId.value = id
            }
        }
    }

    fun addExercise(name: String, sets: Int, reps: Int, weight: Double) {
        val templateId = _templateId.value ?: return
        if (templateId <= 0) return
        viewModelScope.launch {
            val currentExercises = exercises.value ?: emptyList()
            repository.insertExercise(
                ExerciseTemplate(
                    workoutTemplateId = templateId,
                    name = name,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    orderIndex = currentExercises.size
                )
            )
        }
    }

    fun deleteExercise(exercise: ExerciseTemplate) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun updateExercise(exercise: ExerciseTemplate) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }
}

class TemplateEditViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemplateEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemplateEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
