package com.asemlab.quakes.remote.repositories

import android.content.Context
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.toEarthquakeUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EarthquakeManager
@Inject constructor(
    private val earthquakeRepository: EarthquakeRepository,
    private val countriesRepository: CountriesRepository
) {

    private lateinit var countries: List<CountryData>
    private lateinit var states: List<UsaStateData>

    suspend fun getEarthquakes(
        startTime: String,
        endTime: String,
        context: Context,
        onStart: () -> Unit,
        onSuccess: (List<EarthquakesUI>) -> Unit,
        onError: (String?) -> Unit
    ) {
        onStart()
        val earthquakes = earthquakeRepository.getEarthquakes(
            startTime, endTime, onError
        )

        if (!::countries.isInitialized) countries = countriesRepository.getAllCountries(onError)
        if (!::states.isInitialized) states = countriesRepository.getUsaStates(context)

        val uiEvents = earthquakes.map { event ->
            findCountryByEventTitle(event, states, countries)
        }
        onSuccess(uiEvents)
    }

    private fun findCountryByEventTitle(
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

    suspend fun getEarthquakesByMag(
        startTime: String,
        endTime: String,
        maxMagnitude: Double,
        minMagnitude: Double,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<List<EarthquakesUI>> {
        return earthquakeRepository.getEarthquakesByMag(
            startTime, endTime, maxMagnitude, minMagnitude, onStart, onComplete, onError
        ).map {
            it.map { event ->
                findCountryByEventTitle(event, states, countries)
            }
        }
    }

}