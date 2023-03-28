package com.asemlab.quakes.remote.services

import com.asemlab.quakes.database.models.CountryData
import retrofit2.Response
import retrofit2.http.GET

interface CountriesService {

    @GET("all")
    suspend fun getAllCountries(): Response<List<CountryData>>
}