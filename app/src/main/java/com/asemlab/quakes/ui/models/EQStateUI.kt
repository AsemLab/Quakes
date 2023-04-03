package com.asemlab.quakes.ui.models

data class EQStateUI(
    val data: List<EarthquakesUI> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val lastSortBy: EQSort = EQSort.TIME_DEC
)

enum class EQSort{
    TIME, MAG, REGION, TIME_DEC, MAG_DEC, REGION_DEC
}

fun EQSort.toDesc(): EQSort{
    return when(this){
        EQSort.TIME -> EQSort.TIME_DEC
        EQSort.MAG -> EQSort.MAG_DEC
        EQSort.REGION -> EQSort.REGION_DEC
        else ->  EQSort.TIME_DEC
    }
}

fun EQSort.toAsc(): EQSort{
    return when(this){
        EQSort.TIME_DEC -> EQSort.TIME
        EQSort.MAG_DEC -> EQSort.MAG
        EQSort.REGION_DEC -> EQSort.REGION
        else ->  EQSort.TIME
    }
}
