package com.asemlab.quakes.di

import com.asemlab.quakes.database.EarthquakesDao
import com.asemlab.quakes.remote.repositories.EarthquakeRepository
import com.asemlab.quakes.remote.services.EarthquakeService
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
        earthquakesDao: EarthquakesDao,
        earthquakeService: EarthquakeService
    ) = EarthquakeRepository(earthquakesDao, earthquakeService)
}