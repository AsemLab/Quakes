package com.asemlab.quakes.di

import com.asemlab.quakes.database.CountriesDao
import com.asemlab.quakes.database.EarthquakesUIDao
import com.asemlab.quakes.remote.repositories.CountriesRepository
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.asemlab.quakes.remote.repositories.EarthquakeRepository
import com.asemlab.quakes.remote.repositories.GeoLocationRepository
import com.asemlab.quakes.services.CountriesService
import com.asemlab.quakes.services.EarthquakeService
import com.asemlab.quakes.services.GeoLocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun providesEarthquakeRepository(
        earthquakesUIDao: EarthquakesUIDao, earthquakeService: EarthquakeService
    ) = EarthquakeRepository(earthquakesUIDao, earthquakeService)

    @Provides
    @Singleton
    fun providesCountriesRepository(
        countriesDao: CountriesDao, countriesService: CountriesService
    ) = CountriesRepository(countriesDao, countriesService)

    @Provides
    @Singleton
    fun providesGeoLocationRepository(
        geoLocationService: GeoLocationService
    ) = GeoLocationRepository(geoLocationService)

    @Provides
    @Singleton
    fun providesEarthquakeManager(
        earthquakeRepository: EarthquakeRepository,
        countriesRepository: CountriesRepository
    ) = EarthquakeManager(earthquakeRepository, countriesRepository)
}