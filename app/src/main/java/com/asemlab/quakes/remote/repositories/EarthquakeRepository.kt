package com.asemlab.quakes.remote.repositories

import com.asemlab.quakes.database.EarthquakesDao
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.remote.performOnError
import com.asemlab.quakes.remote.performOnSuccess
import com.asemlab.quakes.remote.services.EarthquakeService
import com.asemlab.quakes.utils.performRequest
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val earthquakesDao: EarthquakesDao, private val earthquakeService: EarthquakeService
) {

    suspend fun getEarthquakes(
        startTime: String, endTime: String,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        var earthquakes = earthquakesDao.getAllEarthquakes()
//            emptyList<EarthquakeData>()
//        try {
//            val response = performRequest {
//                earthquakeService.getEarthquakes(startTime, endTime)
//            }
//            response.performOnSuccess {
//                earthquakesDao.clearEarthquakes()
//                earthquakesDao.insertAll(it.features ?: emptyList())
//                earthquakes = it.features!!
//            }.performOnError {
//                onError(it)
//            }
//        } catch (e: Exception) {
//            earthquakes = earthquakesDao.getAllEarthquakes()
//        } finally {
        return earthquakes
//        }

    }

    suspend fun getEarthquakesByMag(
        startTime: String, endTime: String, minMagnitude: Double, maxMagnitude: Double,
        onStart: () -> Unit,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        onStart()
        var earthquakes = earthquakesDao.getAllEarthquakes()
        try {
            val response = performRequest {
                earthquakeService.getEarthquakesByMag(
                    startTime,
                    endTime,
                    minMagnitude,
                    maxMagnitude
                )
            }
            response.performOnSuccess {
                earthquakes = it.features!!.take(20)
            }.performOnError {
                onError(it)
            }
        } catch (e: Exception) {
            earthquakes = earthquakesDao.getAllEarthquakes()
        } finally {
            return earthquakes
        }
    }

    suspend fun clearAllEarthquakes() {
        earthquakesDao.clearEarthquakes()
    }
}