package com.asemlab.quakes.services

import com.asemlab.quakes.models.EarthquakeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface EarthquakeService {

    @GET("query")
    suspend fun getEarthquakes(
        @Query("starttime")
        startTime: String,
        @Query("endtime")
        endTime: String,
        @Query("format")
        format: String = "geojson"
    ): Response<EarthquakeResponse>

    @GET("query")
    suspend fun getEarthquakesByMag(
        @Query("starttime")
        startTime: String,
        @Query("endtime")
        endTime: String,
        @Query("minmagnitude")
        minMagnitude: Double,
        @Query("maxmagnitude")
        maxMagnitude: Double,
        @Query("format")
        format: String = "geojson"
    ): Response<EarthquakeResponse>

}