package com.asemlab.quakes.ui.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.Flags
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "earthquakes_ui")
data class EarthquakesUI(
    var id: String? = null,
    var felt: Int? = null,
    var mag: Double? = null,
    var place: String? = null,
    var time: Long? = null,
    val coordinates: List<Double?>? = null,
    var flags: Flags? = null,
    var name: String? = null,
    var region: String? = null,
    var cca2: String? = null,
    @PrimaryKey(autoGenerate = true)
    var rowId: Long? = null
) : Parcelable

fun EarthquakeData.toEarthquakeUI(countryData: CountryData?): EarthquakesUI {
    return EarthquakesUI(
        serverId,
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