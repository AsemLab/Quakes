package com.asemlab.quakes.ui.search

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asemlab.quakes.R
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.home.HomeViewModel
import com.asemlab.quakes.ui.models.DateRangeValidator
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.isDesc
import com.asemlab.quakes.utils.RANGE_DATE_FORMAT
import com.asemlab.quakes.utils.isConnected
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toSimpleDateFormat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : HomeViewModel(earthquakeManager) {

    private val _tempEvents = mutableListOf<EarthquakesUI>()
    private var _region = "All"
    private val todayDate = Calendar.getInstance()
    lateinit var rangePicker: MaterialDatePicker<Pair<Long, Long>>


    private val fromDate = Calendar.getInstance()
    private val toDate = Calendar.getInstance()
    val fromDateText = MutableLiveData("")
    val toDateText = MutableLiveData("")
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

        if(!isConnected(view.context)) {
            makeToast(view.context, view.context.getString(R.string.no_internet_connection))
            return
        }

        if (fromDate.timeInMillis == toDate.timeInMillis) {
            makeToast(view.context, view.context.getString(R.string.please_select_a_date))
            return
        }

        showStartSearch.postValue(false)

        if (fromDate.after(toDate)) {
            searchEvents(
                toDate.time.toSimpleDateFormat(),
                fromDate.time.toSimpleDateFormat(),
                values[0].toDouble(),
                values[1].toDouble()
            )
        } else {
            searchEvents(
                fromDate.time.toSimpleDateFormat(),
                toDate.time.toSimpleDateFormat(),
                values[0].toDouble(),
                values[1].toDouble()
            )
        }
    }

    fun onMagSliderChanged(slider: RangeSlider, value: Float, fromUser: Boolean) {
        values = slider.values
    }

    fun initializeDateRangePicker(context: Context) {
        val validation = DateRangeValidator(14)
        rangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTextInputFormat(SimpleDateFormat(RANGE_DATE_FORMAT).also {
                it.timeZone = TimeZone.getDefault()
            })
            .setTitleText(context.getString(R.string.select_up_to_14_days))
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(validation)
                    .setOpenAt(todayDate.timeInMillis)
                    .setStart(
                        Date(60, 1, 1).time
                    ).setEnd(
                        todayDate.timeInMillis
                    ).build()
            )
            .build()

        validation.setDatePicker(rangePicker)
        with(rangePicker) {
            addOnPositiveButtonClickListener {
                val f = Date(rangePicker.selection?.first ?: System.currentTimeMillis())
                val s = Date(rangePicker.selection?.second ?: System.currentTimeMillis())


                fromDate.time = f
                fromDateText.postValue(
                    context.getString(
                        R.string.date_format,
                        fromDate.get(Calendar.YEAR),
                        (fromDate.get(Calendar.MONTH) + 1).toString().padStart(2, '0'),
                        fromDate.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    )
                )

                toDate.time = s
                toDateText.postValue(
                    context.getString(
                        R.string.date_format,
                        toDate.get(Calendar.YEAR),
                        (toDate.get(Calendar.MONTH) + 1).toString().padStart(2, '0'),
                        toDate.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    )
                )

            }

        }
    }

}