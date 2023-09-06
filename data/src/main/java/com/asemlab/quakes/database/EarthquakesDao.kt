package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE

@Dao
interface EarthquakesDao {

    @Insert
    suspend fun insertEarthquakesAll(earthquakeData: List<EarthquakeData>)

    @Query("SELECT * FROM earthquakes")
    suspend fun getAllEarthquakes(): List<EarthquakeData>

    @Query("DELETE FROM earthquakes")
    suspend fun clearEarthquakes()

    @Query("select * from earthquakes limit :offset, $DEFAULT_PAGE_SIZE")
    suspend fun getAllEarthquakesByOffset(offset: Int): List<EarthquakeData>

    @Query("DELETE FROM sqlite_sequence WHERE name  = 'earthquakes'")
    suspend fun clearEarthquakesPrimaryKey()

    @Query("SELECT count(rowId) FROM earthquakes")
    suspend fun getEarthquakesSize(): Int

}