package com.asemlab.quakes.ui.home

import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asemlab.quakes.R
import com.asemlab.quakes.model.EQSort
import com.asemlab.quakes.model.isDesc
import com.asemlab.quakes.model.toAsc
import com.asemlab.quakes.model.toDesc
import com.asemlab.quakes.model.toEarthquakeUI
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.models.EQStateUI
import com.asemlab.quakes.utils.toServerDateFormat
import com.asemlab.quakes.utils.tomorrowDate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EQStateUI(isLoading = true))
    var uiState: StateFlow<EQStateUI> = _uiState
    private lateinit var popupMenu: PopupMenu
    var bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED


    fun getLastEarthquakes(context: Context) {
        viewModelScope.launch {
            earthquakeManager.getEarthquakes(Date().toServerDateFormat(),
                tomorrowDate().toServerDateFormat(),
                context,
                onStart = {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(isLoading = true, userMessage = null)
                    }
                },
                onSuccess = {
                    val quakesUi = it.toEarthquakeUI(
                        earthquakeManager.getStates(),
                        earthquakeManager.getCountries()
                    )

                    _uiState.update { currentUiState ->
                        currentUiState.copy(data = quakesUi, isLoading = false, userMessage = null)
                    }
                    sortEarthquakes(descending = _uiState.value.lastSortBy.isDesc())
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

    fun addPopupMenu(view: View, onItemClick: (String) -> Unit) {
        popupMenu = PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                val descending = _uiState.value.lastSortBy.isDesc()
                when (it.itemId) {
                    R.id.sortMag -> {
                        sortEarthquakes(
                            EQSort.MAG,
                            descending
                        )
                        onItemClick(it.title.toString())
                    }

                    R.id.sortTime -> {
                        sortEarthquakes(
                            EQSort.TIME,
                            descending
                        )
                        onItemClick(it.title.toString())
                    }

                    R.id.sortName -> {
                        sortEarthquakes(
                            EQSort.NAME,
                            descending
                        )
                        onItemClick(it.title.toString())
                    }

                    R.id.sortDesc -> {
                        it.isChecked = !it.isChecked
                        sortEarthquakes(
                            descending = it.isChecked
                        )
                    }

                    else -> {}
                }
                return@setOnMenuItemClickListener true
            }
            inflate(R.menu.sort_menu)
            val descending = _uiState.value.lastSortBy.isDesc()
            menu.findItem(R.id.sortDesc).isChecked = descending
        }
    }

    fun showPopupMenu() {
        if (::popupMenu.isInitialized) {
            popupMenu.show()
        }
    }
}