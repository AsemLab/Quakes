package com.asemlab.quakes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "earthquakes_ui")
data class EarthquakesUI(
    var id: String? = null,
    var felt: Int? = null,
    var mag: Double? = null,
    var place: String? = null,
    var time: Long? = null,
    var coordinates: List<Double?>? = null,
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

fun EarthquakeData.findCountryByEventTitle(
    states: List<UsaStateData>, countries: List<CountryData>
): EarthquakesUI {
    val nameFromTitle = properties?.place?.split(", ")?.last()
    val state = states.firstOrNull {
        it.name.equals(nameFromTitle, true) || it.code.equals(nameFromTitle, true)
    }
    val country = if (state == null) {
        countries.firstOrNull {
            it.name?.common?.equals(nameFromTitle, true) ?: false
        }
    } else {
        countries.first {
            it.cca2 == "US"
        }
    }
    return toEarthquakeUI(country)
}

fun List<EarthquakeData>.toEarthquakeUI(states: List<UsaStateData>, countries: List<CountryData>) =
    map { event ->
        event.findCountryByEventTitle(states, countries)
    }
