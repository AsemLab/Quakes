package com.asemlab.quakes.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.models.EQSort
import com.asemlab.quakes.ui.models.EQStateUI
import com.asemlab.quakes.ui.models.toAsc
import com.asemlab.quakes.ui.models.toDesc
import com.asemlab.quakes.utils.toSimpleDateFormat
import com.asemlab.quakes.utils.tomorrowDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : ViewModel() {

    protected val _uiState = MutableStateFlow(EQStateUI(isLoading = true))
    var uiState: StateFlow<EQStateUI> = _uiState

    fun getLastEarthquakes(context: Context) {
        viewModelScope.launch {
            earthquakeManager.getEarthquakes(Date().toSimpleDateFormat(),
                tomorrowDate().toSimpleDateFormat(),
                context,
                onStart = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = true, userMessage = null)
                    }
                },
                onSuccess = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(data = it, isLoading = false, userMessage = null)
                    }
                },
                onError = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, userMessage = it)
                    }
                })
        }
    }

    fun sortEarthquakes(sortBy: EQSort = _uiState.value.lastSortBy, descending: Boolean) {
        _uiState.update { currentUiState ->
            if (descending) {
                currentUiState.copy(lastSortBy = sortBy.toDesc(), data = when (sortBy.toDesc()) {
                    EQSort.MAG_DEC -> currentUiState.data.sortedByDescending { it.mag }
                    EQSort.NAME_DEC -> currentUiState.data.sortedByDescending { it.name }
                    EQSort.TIME_DEC -> currentUiState.data.sortedByDescending { it.time }
                    else -> {
                        currentUiState.data
                    }
                })
            } else {
                currentUiState.copy(lastSortBy = sortBy.toAsc(), data = when (sortBy.toAsc()) {
                    EQSort.MAG -> currentUiState.data.sortedBy { it.mag }
                    EQSort.NAME -> currentUiState.data.sortedBy { it.name }
                    EQSort.TIME -> currentUiState.data.sortedBy { it.time }
                    else -> {
                        currentUiState.data
                    }
                })
            }
        }
    }
}