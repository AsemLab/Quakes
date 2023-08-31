package com.asemlab.quakes.ui.models

import androidx.paging.PagingData

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

enum class EQSort{
    TIME, MAG, NAME, TIME_DEC, MAG_DEC, NAME_DEC
}

fun EQSort.toDesc(): EQSort{
    return when(this){
        EQSort.TIME -> EQSort.TIME_DEC
        EQSort.MAG -> EQSort.MAG_DEC
        EQSort.NAME -> EQSort.NAME_DEC
        else ->  this
    }
}

fun EQSort.toAsc(): EQSort{
    return when(this){
        EQSort.TIME_DEC -> EQSort.TIME
        EQSort.MAG_DEC -> EQSort.MAG
        EQSort.NAME_DEC -> EQSort.NAME
        else ->  this
    }
}
fun EQSort.isDesc(): Boolean{
    return when(this){
        EQSort.TIME_DEC,
        EQSort.MAG_DEC,
        EQSort.NAME_DEC -> true
        else ->  false
    }
}
