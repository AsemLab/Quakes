package com.asemlab.quakes.remote.repositories

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.asemlab.quakes.database.SearchQuakesPagingSource
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EQSort
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.EarthquakesUI
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
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
        onSuccess: (List<EarthquakeData>) -> Unit,
        onError: (String?) -> Unit
    ) {
        onStart()
        val earthquakes = earthquakeRepository.getEarthquakes(
            startTime, endTime, onError
        )

        if (!::countries.isInitialized) countries = countriesRepository.getAllCountries(onError)
        if (!::states.isInitialized) states = countriesRepository.getUsaStates(context)

        onSuccess(earthquakes)
    }

    fun getEarthquakesPagerByMag(
        startTime: String,
        endTime: String,
        minMagnitude: Double,
        maxMagnitude: Double,
        region: String,
        orderBy: EQSort,
        onError: (String?) -> Unit
    ): Flow<PagingData<EarthquakesUI>> {
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchQuakesPagingSource(
                    this,
                    startTime,
                    endTime,
                    minMagnitude,
                    maxMagnitude,
                    onError,
                    region, orderBy
                )
            }
        ).flow
    }

    suspend fun getEarthquakesByMag(
        startTime: String, endTime: String, minMagnitude: Double, maxMagnitude: Double,
        onStart: () -> Unit,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        return earthquakeRepository.getEarthquakesByMag(
            startTime,
            endTime,
            minMagnitude,
            maxMagnitude,
            onStart,
            onError
        )
    }

    fun getCountries() = listOf(*countries.toTypedArray())
    fun getStates() = listOf(*states.toTypedArray())

    suspend fun insertEarthquakes(data: List<EarthquakesUI>) =
        earthquakeRepository.insertEarthquakes(data)

    suspend fun getEarthquakesUISize() = earthquakeRepository.getEarthquakesUISize()

    suspend fun getEarthquakesUIOffset(query: SupportSQLiteQuery): List<EarthquakesUI> {
        return earthquakeRepository.getEarthquakesUIOffset(query)
    }

    fun getOrderByQuery(region: String, sort: EQSort, offset: Int): SimpleSQLiteQuery {
        return earthquakeRepository.getOrderByQuery(region, sort, offset)
    }

    suspend fun clearAllEarthquakes() {
        earthquakeRepository.clearAllEarthquakes()
    }
}