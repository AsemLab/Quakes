package com.asemlab.quakes.remote.repositories

import com.asemlab.quakes.database.EarthquakesDao
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.remote.performOnError
import com.asemlab.quakes.remote.performOnSuccess
import com.asemlab.quakes.remote.services.EarthquakeService
import com.asemlab.quakes.utils.performRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val earthquakesDao: EarthquakesDao, private val earthquakeService: EarthquakeService
) {

    suspend fun getEarthquakes(
        startTime: String, endTime: String,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        var earthquakes = earthquakesDao.getAllEarthquakes()
        return earthquakes.ifEmpty {
            val response = performRequest {
                earthquakeService.getEarthquakes(startTime, endTime)
            }
            response.performOnSuccess {
                earthquakesDao.insertAll(it.features ?: emptyList())
                earthquakes = it.features!!
            }.performOnError {
                onError(it)
            }
            earthquakes
        }
    }

    suspend fun getEarthquakesByMag(
        startTime: String, endTime: String, maxMagnitude: Double, minMagnitude: Double,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<List<EarthquakeData>> {
        return flow {
            val response = performRequest {
                earthquakeService.getEarthquakesByMag(
                    startTime,
                    endTime,
                    maxMagnitude,
                    minMagnitude
                )
            }
            response.performOnSuccess {
                emit(it.features!!)
            }.performOnError {
                onError(it)
            }
        }.onStart { onStart() }.onCompletion { onComplete() }
    }

    suspend fun clearAllEarthquakes() {
        earthquakesDao.clearEarthquakes()
    }
}