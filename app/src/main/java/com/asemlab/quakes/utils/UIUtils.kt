package com.asemlab.quakes.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.toEarthquakeUI

fun makeToast(context: Context, m: String) {
    Toast.makeText(context, m, Toast.LENGTH_SHORT).show()
}

fun makeLongToast(context: Context, m: String) {
    Toast.makeText(context, m, Toast.LENGTH_LONG).show()
}

fun isNightModeOn(): Boolean {
    val nightModeFlags = Resources.getSystem().configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

fun findCountryByEventTitle(
    event: EarthquakeData, states: List<UsaStateData>, countries: List<CountryData>
): EarthquakesUI {
    val nameFromTitle = event.properties?.place?.split(", ")?.last()
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
    return event.toEarthquakeUI(country)
}


fun List<EarthquakeData>.toEarthquakeUI(states: List<UsaStateData>, countries: List<CountryData>) =
    map { event ->
        findCountryByEventTitle(event, states, countries)
    }
