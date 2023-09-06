package com.asemlab.quakes.remote.services

import com.asemlab.quakes.database.models.GeoLocationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoLocationService {

    @GET("reverse")
    suspend fun getLocationInfo(
        @Query("key")
        key: String,
        @Query("lon")
        lon: Double,
        @Query("lat")
        lat: Double,
        @Query("format")
        format: String = "json"
    ): Response<GeoLocationData>
}