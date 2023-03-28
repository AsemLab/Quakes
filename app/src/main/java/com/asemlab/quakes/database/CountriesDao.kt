package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.CountryData
import kotlinx.coroutines.flow.Flow

@Dao
interface CountriesDao {

    @Insert
    suspend fun insertAll(countryData: List<CountryData>)

    @Query("SELECT * FROM countries")
    suspend fun getAllCountries(): Flow<List<CountryData>>

    @Query("DELETE FROM countries")
    suspend fun clearCountries()
}