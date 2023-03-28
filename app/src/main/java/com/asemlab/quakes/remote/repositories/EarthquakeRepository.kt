package com.asemlab.quakes.remote.repositories

import com.asemlab.quakes.database.EarthquakesDao
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.remote.services.EarthquakeService
import com.asemlab.quakes.utils.performRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val earthquakesDao: EarthquakesDao, private val earthquakeService: EarthquakeService
) {

    suspend fun getEarthquakes(startTime: String, endTime: String): Flow<List<EarthquakeData>> {
        val earthquakes = earthquakesDao.getAllEarthquakes()
        return if (earthquakes.first().isEmpty()) {
            val response = performRequest {
                earthquakeService.getEarthquakes(startTime, endTime)
            }
            response?.let {
                earthquakesDao.insertAll(it.features ?: emptyList())
            }
            flowOf(response?.features ?: emptyList())
        } else {
            earthquakes
        }
    }

    suspend fun getEarthquakesByMag(
        startTime: String, endTime: String, maxMagnitude: Double, minMagnitude: Double
    ): Flow<List<EarthquakeData>> {
        val response = performRequest {
            earthquakeService.getEarthquakesByMag(startTime, endTime, maxMagnitude, minMagnitude)
        }
        return flowOf(response?.features ?: emptyList())
    }

}