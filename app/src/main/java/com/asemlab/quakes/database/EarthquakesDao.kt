package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.EarthquakeData
import kotlinx.coroutines.flow.Flow

@Dao
interface EarthquakesDao {

    @Insert
    suspend fun insertAll(earthquakeData: List<EarthquakeData>)

    @Query("SELECT * FROM earthquakes")
    suspend fun getAllEarthquakes(): Flow<List<EarthquakeData>>

    @Query("DELETE FROM earthquakes")
    suspend fun clearEarthquakes()
}