package com.asemlab.quakes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.database.typeconverters.FlagsConverter
import com.asemlab.quakes.database.typeconverters.GeometryConverter
import com.asemlab.quakes.database.typeconverters.NameConverter
import com.asemlab.quakes.database.typeconverters.PropertiesConverter

@Database(
    entities = [EarthquakeData::class, CountryData::class, UsaStateData::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(value = [FlagsConverter::class, GeometryConverter::class, NameConverter::class, PropertiesConverter::class])
abstract class EarthquakesDB : RoomDatabase() {

    abstract fun getEarthquakesDao(): EarthquakesDao
    abstract fun getCountriesDao(): CountriesDao

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