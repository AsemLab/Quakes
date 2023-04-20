package com.asemlab.quakes.ui.models

import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.Flags

data class EarthquakesUI(
    val felt: Int? = null,
    val mag: Double? = null,
    val place: String? = null,
    val time: Long? = null,
    val coordinates: List<Double?>? = null,
    val flags: Flags? = null,
    val name: String? = null,
    val region: String? = null,
    val cca2: String? = null
)

fun EarthquakeData.toEarthquakeUI(countryData: CountryData?): EarthquakesUI {
    return EarthquakesUI(
        properties?.felt,
        properties?.mag,
        properties?.place,
        properties?.time,
        geometry?.coordinates,
        countryData?.flags,
        countryData?.name?.common ?: "Global",
        countryData?.region,
        countryData?.cca2
    )
}