package com.asemlab.quakes.ui.models

data class EQStateUI(
    val data: List<EarthquakesUI> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val lastSortBy: EQSort = EQSort.TIME_DEC
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