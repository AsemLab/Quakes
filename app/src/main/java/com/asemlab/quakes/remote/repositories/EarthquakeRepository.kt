package com.asemlab.quakes.remote.repositories

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.asemlab.quakes.database.EarthquakesUIDao
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.remote.performOnError
import com.asemlab.quakes.remote.performOnSuccess
import com.asemlab.quakes.remote.services.EarthquakeService
import com.asemlab.quakes.ui.models.EQSort
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.utils.performRequest
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val earthquakesUIDao: EarthquakesUIDao, private val earthquakeService: EarthquakeService
) {

    suspend fun getEarthquakes(
        startTime: String, endTime: String,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        var earthquakes = emptyList<EarthquakeData>()
        try {
            val response = performRequest {
                earthquakeService.getEarthquakes(startTime, endTime)
            }
            response.performOnSuccess {
                earthquakes = it.features!!
            }.performOnError {
                onError(it)
            }
        } catch (e: Exception) {
            Log.e("getEarthquakes", "${e.message}")
            onError(e.message)
        } finally {
            return earthquakes
        }

    }

    suspend fun getEarthquakesByMag(
        startTime: String, endTime: String, minMagnitude: Double, maxMagnitude: Double,
        onStart: () -> Unit,
        onError: (String?) -> Unit
    ): List<EarthquakeData> {
        onStart()
        var earthquakes = emptyList<EarthquakeData>()
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
                earthquakes = it.features!!
            }.performOnError {
                onError(it)
            }
        } catch (e: Exception) {
            Log.e("getEarthquakesByMag", "${e.message}")
            onError(e.message)
        } finally {
            return earthquakes
        }
    }

    suspend fun insertEarthquakes(data: List<EarthquakesUI>) =
        earthquakesUIDao.insertEarthquakesUIAll(data)

    suspend fun getEarthquakesUISize() = earthquakesUIDao.getEarthquakesUISize()

    suspend fun getEarthquakesUIOffset(query: SupportSQLiteQuery): List<EarthquakesUI> {
        return earthquakesUIDao.getEarthquakesUIOffset(query)
    }

    fun getOrderByQuery(region: String, sort: EQSort, offset: Int): SimpleSQLiteQuery {
        return earthquakesUIDao.getOrderByQuery(region, sort, offset)
    }

    suspend fun clearAllEarthquakes() {
        with(earthquakesUIDao) {
            clearEarthquakesUI()
            clearEarthquakesUIPrimaryKey()
        }
    }

}