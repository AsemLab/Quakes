package com.asemlab.quakes.ui.search

import androidx.lifecycle.viewModelScope
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.home.HomeViewModel
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.isDesc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : HomeViewModel(earthquakeManager) {

    private val _tempEvents = mutableListOf<EarthquakesUI>()
    private var _region = "All"

    fun searchEvents(
        startTime: String,
        endTime: String,
        minMagnitude: Double,
        maxMagnitude: Double
    ) {
        viewModelScope.launch {
            earthquakeManager.getEarthquakesByMag(startTime, endTime, minMagnitude, maxMagnitude,
                onStart = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = true, userMessage = null)
                    }
                },
                onSuccess = {
                    _tempEvents.clear()
                    _tempEvents.addAll(it)
                    filterByRegion(_region)
                    sortEarthquakes(descending = _uiState.value.lastSortBy.isDesc())
                },
                onError = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, userMessage = it)
                    }
                }
            )
        }
    }

    fun filterByRegion(region: String) {
        _region = region
        _uiState.update { currentUiState ->
            currentUiState.copy(
                data =
                if (region == "All")
                    _tempEvents
                else
                    _tempEvents.filter { it.region == region },
                isLoading = false,
                lastSortBy = _uiState.value.lastSortBy
            )
        }
    }
}