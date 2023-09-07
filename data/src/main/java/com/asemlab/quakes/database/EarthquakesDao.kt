package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asemlab.quakes.database.models.EarthquakeData
import com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE

@Dao
interface EarthquakesDao {

    @Insert
    fun insertEarthquakesAll(earthquakeData: List<EarthquakeData>)

    @Query("SELECT * FROM earthquakes")
    fun getAllEarthquakes(): List<EarthquakeData>

    @Query("DELETE FROM earthquakes")
    fun clearEarthquakes()

    @Query("select * from earthquakes limit :offset, $DEFAULT_PAGE_SIZE")
    fun getAllEarthquakesByOffset(offset: Int): List<EarthquakeData>

    @Query("DELETE FROM sqlite_sequence WHERE name  = 'earthquakes'")
    fun clearEarthquakesPrimaryKey()

    @Query("SELECT count(rowId) FROM earthquakes")
    fun getEarthquakesSize(): Int

}