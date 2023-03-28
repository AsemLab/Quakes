package com.asemlab.quakes.remote.services

import com.asemlab.quakes.remote.models.EarthquakeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface EarthquakeService {

    @GET("/query")
    suspend fun getEarthquakes(
        @Query("starttime")
        startTime: String,
        @Query("endtime")
        endTime: String,
        format: String = "geojson"
    ): Response<EarthquakeResponse>

    @GET("/query")
    suspend fun getEarthquakesByMag(
        @Query("starttime")
        startTime: String,
        @Query("endtime")
        endTime: String,
        @Query("maxmagnitude")
        maxMagnitude: Double,
        @Query("minmagnitude")
        minMagnitude: Double,
        format: String = "geojson"
    ): Response<EarthquakeResponse>

}