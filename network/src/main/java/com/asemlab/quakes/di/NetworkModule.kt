package com.asemlab.quakes.di


import com.asemlab.quakes.network.BuildConfig
import com.asemlab.quakes.services.CountriesService
import com.asemlab.quakes.services.EarthquakeService
import com.asemlab.quakes.services.GeoLocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    annotation class EarthquakesAnnotation

    @Qualifier
    annotation class CountriesAnnotation

    @Qualifier
    annotation class GeoLocationAnnotation

    private val EARTHQUAKE_BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/"
    private val COUNTRIES_BASE_URL = "https://restcountries.com/v3.1/"
    private val GEOLOCATION_BASE_URL = "https://us1.locationiq.com/v1/"

    @Provides
    @Singleton
    fun providesOkhttpClient() = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }
            addNetworkInterceptor(logInterceptor)
        }
    }.build()

    @Provides
    @Singleton
    @EarthquakesAnnotation
    fun providesEarthquakesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(EARTHQUAKE_BASE_URL).build()

    @Provides
    @Singleton
    fun providesEarthquakesService(@EarthquakesAnnotation retrofit: Retrofit) =
        retrofit.create(EarthquakeService::class.java)

    @Provides
    @Singleton
    @CountriesAnnotation
    fun providesCountriesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(COUNTRIES_BASE_URL).build()

    @Provides
    @Singleton
    fun providesCountriesService(@CountriesAnnotation retrofit: Retrofit) =
        retrofit.create(CountriesService::class.java)

    @Provides
    @Singleton
    @GeoLocationAnnotation
    fun providesGeoLocationRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(GEOLOCATION_BASE_URL).build()

    @Provides
    @Singleton
    fun providesGeoLocationService(@GeoLocationAnnotation retrofit: Retrofit) =
        retrofit.create(GeoLocationService::class.java)
}