package com.asemlab.quakes.ui.search

import android.app.DatePickerDialog
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asemlab.quakes.R
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.home.HomeViewModel
import com.asemlab.quakes.ui.models.EQSort
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.isDesc
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toSimpleDateFormat
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : HomeViewModel(earthquakeManager) {

    private val _tempEvents = mutableListOf<EarthquakesUI>()
    private var _region = "All"
    private val maxDate = Calendar.getInstance()
    private val minDate = Calendar.getInstance().apply {
        set(1960, 0, 1)
    }

    private val from = Calendar.getInstance()
    private val to = Calendar.getInstance()
    val fromDate = MutableLiveData("")
    val toDate = MutableLiveData("")
    val sortText = MutableLiveData("")
    val showStartSearch = MutableLiveData(true)
    private var values = listOf(0f, 8f)


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

    private fun filterByRegion(region: String) {
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


    fun showFromDatePicker(tv: View) {
        with(tv as TextView) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    from.set(year, month, dayOfMonth)
                    fromDate.postValue(
                        context.getString(
                            R.string.date_format,
                            year,
                            month + 1,
                            dayOfMonth
                        )
                    )
                },
                maxDate.get(Calendar.YEAR),
                maxDate.get(Calendar.MONTH),
                maxDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = maxDate.time.time
                datePicker.minDate = minDate.time.time
            }.show()
        }
    }

    fun showToDatePicker(tv: View) {
        with(tv as TextView) {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    text = context.getString(R.string.date_format, year, month + 1, dayOfMonth)
                    to.set(year, month, dayOfMonth)
                    toDate.postValue(
                        context.getString(
                            R.string.date_format,
                            year,
                            month + 1,
                            dayOfMonth
                        )
                    )
                },
                maxDate.get(Calendar.YEAR),
                maxDate.get(Calendar.MONTH),
                maxDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = maxDate.time.time
                datePicker.minDate = minDate.time.time
            }.show()
        }
    }

    fun onRegionSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        view?.resources?.getStringArray(R.array.regions)?.let {
            filterByRegion(it[position])
            sortEarthquakes(descending = _uiState.value.lastSortBy.isDesc())
        }
    }

    fun onSearch(view: View) {
        if (from.timeInMillis == to.timeInMillis) {
            makeToast(view.context, view.context.getString(R.string.please_select_a_date))
            return
        }

        showStartSearch.postValue(false)

        if (from.after(to)) {
            searchEvents(
                to.time.toSimpleDateFormat(),
                from.time.toSimpleDateFormat(),
                values[0].toDouble(),
                values[1].toDouble()
            )
        } else {
            searchEvents(
                from.time.toSimpleDateFormat(),
                to.time.toSimpleDateFormat(),
                values[0].toDouble(),
                values[1].toDouble()
            )
        }
    }

    fun onMagSliderChanged(slider: RangeSlider, value: Float, fromUser: Boolean) {
        values = slider.values
    }


}