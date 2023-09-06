package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.UsaStateData

@Dao
interface CountriesDao {

    @Insert
    suspend fun insertAll(countryData: List<CountryData>)

    @Query("SELECT * FROM countries")
    suspend fun getAllCountries(): List<CountryData>

    @Query("DELETE FROM countries")
    suspend fun clearCountries()

    @Insert
    suspend fun insertAllStates(states: List<UsaStateData>)

    @Query("SELECT * FROM usa_states")
    suspend fun getAllStates(): List<UsaStateData>
}