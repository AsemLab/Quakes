package com.asemlab.quakes.ui.search

import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.asemlab.quakes.R
import com.asemlab.quakes.model.EQSort
import com.asemlab.quakes.model.isDesc
import com.asemlab.quakes.model.toAsc
import com.asemlab.quakes.model.toDesc
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.ui.models.DateRangeValidator
import com.asemlab.quakes.ui.models.EQSearchStateUI
import com.asemlab.quakes.utils.RANGE_DATE_FORMAT
import com.asemlab.quakes.utils.isConnected
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toSimpleDateFormat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val earthquakeManager: EarthquakeManager
) : ViewModel() {

    var region = MutableLiveData("All")
    private val todayDate = Calendar.getInstance()
    lateinit var rangePicker: MaterialDatePicker<Pair<Long, Long>>
    private lateinit var regionPopupMenu: PopupMenu
    private lateinit var sortPopupMenu: PopupMenu
    var isCollapsed = MutableLiveData(false)
    val uiState = MutableStateFlow(EQSearchStateUI())


    private val fromDate = Calendar.getInstance()
    private val toDate = Calendar.getInstance()
    private val sort = MutableLiveData(EQSort.TIME_DEC)
    val fromDateText = MutableLiveData("")
    val toDateText = MutableLiveData("")
    val sortText = MutableLiveData("Time")
    val showStartSearch = MutableLiveData(true)
    private var values = listOf(0f, 8f)


    fun searchEvents(
        startTime: String,
        endTime: String,
        minMagnitude: Double,
        maxMagnitude: Double,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            uiState.emit(EQSearchStateUI(data = earthquakeManager.getEarthquakesPagerByMag(
                startTime,
                endTime,
                minMagnitude,
                maxMagnitude,
                region.value ?: "All",
                sort.value ?: EQSort.TIME_DEC
            ) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        onError(it ?: "Error fetch data")
                    }
                }
            }.cachedIn(viewModelScope).first(), userMessage = null
            )
            )
        }
    }


    private fun filterByRegion(region: String, onError: (String) -> Unit) {
        this.region.value = region

        if (fromDate.timeInMillis == toDate.timeInMillis || showStartSearch.value!!) {
            return
        }

        searchEvents(
            fromDate.time.toSimpleDateFormat(),
            toDate.time.toSimpleDateFormat(),
            values[0].toDouble(),
            values[1].toDouble(),
            onError
        )
    }

    fun onSearch(view: View) {

        if (!isConnected(view.context)) {
            makeToast(
                view.context,
                view.context.getString(R.string.no_internet_connection)
            )
            return
        }

        if (fromDate.timeInMillis == toDate.timeInMillis) {
            makeToast(
                view.context,
                view.context.getString(R.string.please_select_a_date)
            )
            return
        }
        clearData()

        showStartSearch.postValue(false)

        searchEvents(
            fromDate.time.toSimpleDateFormat(),
            toDate.time.toSimpleDateFormat(),
            values[0].toDouble(),
            values[1].toDouble()
        ) { makeToast(view.context, it) }
    }

    private fun clearData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                earthquakeManager.clearAllEarthquakes()
            }
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
            .setPositiveButtonText(context.getString(R.string.confirm))
            .setTitleText(context.getString(R.string.select_up_to_14_days)).setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(validation)
                    .setOpenAt(todayDate.timeInMillis).setStart(
                        Date(60, 1, 1).time
                    ).setEnd(
                        todayDate.timeInMillis
                    ).build()
            ).build()

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

    fun addRegionPopupMenu(view: View, onItemClick: (String) -> Unit) {
        regionPopupMenu = PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                onItemClick(it.title.toString())
                filterByRegion(it.title.toString()) { e ->
                    makeToast(
                        view.context,
                        e
                    )
                }
                return@setOnMenuItemClickListener true
            }
            inflate(R.menu.region_menu)
        }
    }

    fun showRegionPopupMenu() {
        if (::regionPopupMenu.isInitialized) {
            regionPopupMenu.show()
        }
    }

    fun addPopupMenu(view: View, onItemClick: (String) -> Unit) {
        sortPopupMenu = PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                val descending = sort.value?.isDesc() ?: true
                when (it.itemId) {
                    R.id.sortMag -> {
                        sortEarthquakes(
                            EQSort.MAG, descending
                        ) { e -> makeToast(view.context, e) }
                        onItemClick(it.title.toString())
                    }

                    R.id.sortTime -> {
                        sortEarthquakes(
                            EQSort.TIME, descending
                        ) { e -> makeToast(view.context, e) }
                        onItemClick(it.title.toString())
                    }

                    R.id.sortName -> {
                        sortEarthquakes(
                            EQSort.NAME, descending
                        ) { e -> makeToast(view.context, e) }
                        onItemClick(it.title.toString())
                    }

                    R.id.sortDesc -> {
                        it.isChecked = !it.isChecked
                        sortEarthquakes(
                            descending = it.isChecked
                        ) { e -> makeToast(view.context, e) }
                    }

                    else -> {}
                }
                return@setOnMenuItemClickListener true
            }
            inflate(R.menu.sort_menu)
            val descending = sort.value?.isDesc() ?: true
            menu.findItem(R.id.sortDesc).isChecked = descending
        }
    }

    private fun sortEarthquakes(
        sort: EQSort = this.sort.value ?: EQSort.TIME_DEC,
        descending: Boolean,
        onError: (String) -> Unit
    ) {
        this.sort.value = if (descending) sort.toDesc() else sort.toAsc()
        if (fromDate.timeInMillis == toDate.timeInMillis || showStartSearch.value!!) {
            return
        }
        searchEvents(
            fromDate.time.toSimpleDateFormat(),
            toDate.time.toSimpleDateFormat(),
            values[0].toDouble(),
            values[1].toDouble(),
            onError
        )
    }

    fun showSortPopupMenu() {
        if (::sortPopupMenu.isInitialized) {
            sortPopupMenu.show()
        }
    }

}