package com.asemlab.quakes.remote.repositories

import com.asemlab.quakes.BuildConfig
import com.asemlab.quakes.model.GeoLocationData
import com.asemlab.quakes.models.performOnError
import com.asemlab.quakes.models.performOnSuccess
import com.asemlab.quakes.services.GeoLocationService
import com.asemlab.quakes.utils.performRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GeoLocationRepository @Inject constructor(
    private val geoLocationService: GeoLocationService
) {

    suspend fun getLocationInfo(
        lon: Double,
        lat: Double,
        onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit
    ): Flow<GeoLocationData> {
        return flow {
            val response = performRequest {
                geoLocationService.getLocationInfo(BuildConfig.LOCATION_IQ_KEY, lon, lat)
            }
            response.performOnSuccess {
                emit(it)
            }.performOnError {
                onError(it)
            }
        }.onStart { onStart() }.onCompletion { onComplete() }
    }

}