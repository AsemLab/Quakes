package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.EarthquakeDate
import kotlinx.coroutines.flow.Flow

@Dao
interface EarthquakesDao {

    @Insert
    suspend fun insertAll(earthquakeDate: List<EarthquakeDate>)

    @Query("SELECT * FROM earthquakes")
    suspend fun getAllEarthquakes(): Flow<List<EarthquakeDate>>

    @Query("DELETE FROM earthquakes")
    suspend fun clearEarthquakes()
}