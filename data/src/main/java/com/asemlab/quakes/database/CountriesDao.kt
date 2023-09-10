package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.model.CountryData
import com.asemlab.quakes.model.UsaStateData

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