package com.asemlab.quakes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.asemlab.quakes.model.CountryData
import com.asemlab.quakes.model.EarthquakeData
import com.asemlab.quakes.model.EarthquakesUI
import com.asemlab.quakes.model.UsaStateData
import com.asemlab.quakes.database.typeconverters.CoordinatesConverter
import com.asemlab.quakes.database.typeconverters.FlagsConverter
import com.asemlab.quakes.database.typeconverters.GeometryConverter
import com.asemlab.quakes.database.typeconverters.NameConverter
import com.asemlab.quakes.database.typeconverters.PropertiesConverter

@Database(
    entities = [EarthquakeData::class, CountryData::class, UsaStateData::class, EarthquakesUI::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [FlagsConverter::class, GeometryConverter::class, NameConverter::class, PropertiesConverter::class, CoordinatesConverter::class])
abstract class EarthquakesDB : RoomDatabase() {

    abstract fun getEarthquakesDao(): EarthquakesDao
    abstract fun getCountriesDao(): CountriesDao
    abstract fun getEarthquakesUIDao(): EarthquakesUIDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `usa_states` ( `code` TEXT, `name` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT)"
                )
            }
        }
    }

}