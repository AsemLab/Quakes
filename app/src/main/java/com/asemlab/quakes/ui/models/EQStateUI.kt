package com.asemlab.quakes.ui.models

import androidx.paging.PagingData
import com.asemlab.quakes.database.models.EQSort
import com.asemlab.quakes.database.models.EarthquakesUI

data class EQStateUI(
    val data: List<EarthquakesUI> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val lastSortBy: EQSort = EQSort.TIME_DEC
)

data class EQSearchStateUI(
    val data: PagingData<EarthquakesUI>? = null,
    val userMessage: String? = null
)