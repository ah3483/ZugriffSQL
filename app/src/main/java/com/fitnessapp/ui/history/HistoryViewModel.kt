package com.fitnessapp.ui.history

import androidx.lifecycle.*
import com.fitnessapp.data.model.WorkoutSession
import com.fitnessapp.data.model.WorkoutSessionWithExercises
import com.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allSessions: LiveData<List<WorkoutSession>> = repository.allSessions

    private val _sessionId = MutableLiveData<Long>()

    val sessionDetail: LiveData<WorkoutSessionWithExercises?> = _sessionId.switchMap { id ->
        repository.getSessionWithExercises(id)
    }

    fun loadSessionDetail(sessionId: Long) {
        _sessionId.value = sessionId
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}

class HistoryViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
