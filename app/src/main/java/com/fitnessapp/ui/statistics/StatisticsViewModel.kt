package com.fitnessapp.ui.statistics

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.fitnessapp.FitnessApp
import com.fitnessapp.data.ImportResult
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FitnessApp).repository
    private val dbManager = (application as FitnessApp).databaseAccessManager

    val summary = repository.workoutSummary
    val personalRecords = repository.personalRecords
    val monthlyStats = repository.monthlyStats
    val exerciseNames = repository.allExerciseNames
    val totalSessions = repository.totalCompletedSessions
    val mostUsedTemplate = repository.mostUsedTemplate
    val totalVolume = repository.totalVolumeKg

    private val _selectedExercise = MutableLiveData<String>()
    val exerciseProgress = _selectedExercise.switchMap { name ->
        repository.getExerciseProgress(name)
    }

    private val _last30DaysSessions = repository.getSessionCountSince(
        System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
    )
    val last30DaysSessions: LiveData<Int> = _last30DaysSessions

    private val _exportResult = MutableLiveData<String?>()
    val exportResult: LiveData<String?> = _exportResult

    private val _importResult = MutableLiveData<ImportResult?>()
    val importResult: LiveData<ImportResult?> = _importResult

    fun selectExercise(name: String) {
        _selectedExercise.value = name
    }

    fun exportDatabase() {
        viewModelScope.launch {
            try {
                val json = dbManager.exportToJson()
                _exportResult.value = json
            } catch (e: Exception) {
                _exportResult.value = null
            }
        }
    }

    fun importDatabase(uri: Uri) {
        viewModelScope.launch {
            val result = dbManager.importFromJson(getApplication(), uri)
            _importResult.value = result
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }

    fun clearImportResult() {
        _importResult.value = null
    }
}
