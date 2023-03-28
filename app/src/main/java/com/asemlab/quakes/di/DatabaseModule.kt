package com.asemlab.quakes.di

import android.content.Context
import androidx.room.Room
import com.asemlab.quakes.database.EarthquakesDB
import com.asemlab.quakes.database.typeconverters.FlagsConverter
import com.asemlab.quakes.database.typeconverters.GeometryConverter
import com.asemlab.quakes.database.typeconverters.NameConverter
import com.asemlab.quakes.database.typeconverters.PropertiesConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideEarthquakeDB(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, EarthquakesDB::class.java, "earthquakes_db")
            .addTypeConverter(PropertiesConverter::class.java)
            .addTypeConverter(GeometryConverter::class.java)
            .addTypeConverter(FlagsConverter::class.java)
            .addTypeConverter(NameConverter::class.java)
            .build()

    @Provides
    fun providerEarthquakesDao(earthquakesDB: EarthquakesDB) = earthquakesDB.getEarthquakesDao()

    @Provides
    fun providerCountriesDao(earthquakesDB: EarthquakesDB) = earthquakesDB.getCountriesDao()

}