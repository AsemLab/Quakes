package com.asemlab.quakes.services

import com.asemlab.quakes.model.CountryData
import retrofit2.Response
import retrofit2.http.GET

interface CountriesService {

    @GET("all?fields=name,flags,cca2,region")
    suspend fun getAllCountries(): Response<List<CountryData>>
}