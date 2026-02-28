package com.fitnessapp.ui.templates

import androidx.lifecycle.*
import com.fitnessapp.data.model.WorkoutTemplate
import com.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class TemplateListViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allTemplates: LiveData<List<WorkoutTemplate>> = repository.allTemplates

    fun deleteTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
        }
    }
}

class TemplateListViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TemplateListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TemplateListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
