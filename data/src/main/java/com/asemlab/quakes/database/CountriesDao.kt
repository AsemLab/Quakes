package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.UsaStateData

@Dao
interface CountriesDao {

    @Insert
    fun insertAll(countryData: List<CountryData>)

    @Query("SELECT * FROM countries")
    fun getAllCountries(): List<CountryData>

    @Query("DELETE FROM countries")
    fun clearCountries()

    @Insert
    fun insertAllStates(states: List<UsaStateData>)

    @Query("SELECT * FROM usa_states")
    fun getAllStates(): List<UsaStateData>
}