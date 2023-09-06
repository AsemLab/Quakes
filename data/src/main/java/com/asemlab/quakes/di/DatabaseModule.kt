package com.asemlab.quakes.di

import android.content.Context
import androidx.room.Room
import com.asemlab.quakes.database.EarthquakesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideEarthquakeDB(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, EarthquakesDB::class.java, "earthquakes_db")
            .build()

    @Provides
    @Singleton
    fun providerEarthquakesDao(earthquakesDB: EarthquakesDB) = earthquakesDB.getEarthquakesDao()

    @Provides
    @Singleton
    fun providerCountriesDao(earthquakesDB: EarthquakesDB) = earthquakesDB.getCountriesDao()

    @Provides
    @Singleton
    fun providerEarthquakesUIDao(earthquakesDB: EarthquakesDB) = earthquakesDB.getEarthquakesUIDao()

}