package com.asemlab.quakes.di

import com.asemlab.quakes.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier


@Module
@InstallIn(ServiceComponent::class)
class NetworkModule {

    @Qualifier
    annotation class EarthquakesService

    @Qualifier
    annotation class CountriesService

    @Qualifier
    annotation class GeoLocationService

    private val EARTHQUAKE_BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1"
    private val COUNTRIES_BASE_URL = "https://restcountries.com/v3.1"
    private val GEOLOCATION_BASE_URL = "https://us1.locationiq.com/v1"

    @Provides
    fun providesOkhttpClient() = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addNetworkInterceptor(logInterceptor)
        }
    }.build()

    @Provides
    @EarthquakesService
    fun providesEarthquakesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(EARTHQUAKE_BASE_URL).build()

    @Provides
    @EarthquakesService
    fun providesEarthquakesService(retrofit: Retrofit) =
        retrofit.create(EarthquakesService::class.java)

    @Provides
    @CountriesService
    fun providesCountriesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(COUNTRIES_BASE_URL).build()

    @Provides
    @CountriesService
    fun providesCountriesService(retrofit: Retrofit) =
        retrofit.create(CountriesService::class.java)

    @Provides
    @GeoLocationService
    fun providesGeoLocationRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(GEOLOCATION_BASE_URL).build()

    @Provides
    @GeoLocationService
    fun providesGeoLocationService(retrofit: Retrofit) =
        retrofit.create(GeoLocationService::class.java)
}