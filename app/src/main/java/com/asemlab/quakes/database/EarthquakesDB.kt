package com.asemlab.quakes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeDate
import com.asemlab.quakes.database.typeconverters.FlagsConverter
import com.asemlab.quakes.database.typeconverters.GeometryConverter
import com.asemlab.quakes.database.typeconverters.NameConverter
import com.asemlab.quakes.database.typeconverters.PropertiesConverter

@Database(entities = [EarthquakeDate::class, CountryData::class], version = 1)
@TypeConverters(value = [FlagsConverter::class, GeometryConverter::class, NameConverter::class, PropertiesConverter::class])
abstract class EarthquakesDB : RoomDatabase() {

    abstract fun getEarthquakesDao(): EarthquakesDao
    abstract fun getCountriesDao(): CountriesDao
}