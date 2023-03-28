package com.asemlab.quakes.remote.services

import com.asemlab.quakes.database.models.GeoLocationData
import retrofit2.Response
import retrofit2.http.GET

interface GeoLocationService {

    @GET("reverse")
    suspend fun getLocationInfo(
        key: String,
        lon: Double,
        lat: Double,
        format: String = "json"
    ): Response<GeoLocationData>
}