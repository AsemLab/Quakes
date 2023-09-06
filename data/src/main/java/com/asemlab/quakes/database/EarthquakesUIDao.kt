package com.asemlab.quakes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.asemlab.quakes.database.models.EQSort
import com.asemlab.quakes.database.models.EarthquakesUI
import com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE

@Dao
interface EarthquakesUIDao {

    @Insert
    suspend fun insertEarthquakesUIAll(earthquakeUI: List<EarthquakesUI>)

    @Query("SELECT * FROM earthquakes_ui")
    suspend fun getAllEarthquakesUI(): List<EarthquakesUI>

    @Query("DELETE FROM earthquakes_ui")
    suspend fun clearEarthquakesUI()

    @RawQuery
    suspend fun getEarthquakesUIOffset(query: SupportSQLiteQuery): List<EarthquakesUI>

    @Query("DELETE FROM sqlite_sequence WHERE name  = 'earthquakes_ui'")
    suspend fun clearEarthquakesUIPrimaryKey()

    @Query("SELECT count(*) FROM earthquakes_ui")
    suspend fun getEarthquakesUISize(): Int

    fun getOrderByQuery(region: String, sort: EQSort, offset: Int): SimpleSQLiteQuery {
        val orderBy = when (sort) {
            EQSort.TIME -> "time"
            EQSort.MAG -> "mag"
            EQSort.NAME -> "name"
            EQSort.TIME_DEC -> "time desc"
            EQSort.MAG_DEC -> "mag desc"
            EQSort.NAME_DEC -> "name desc"
        }

        val formattedRegion = if (region == "All") "%%" else region

        return SimpleSQLiteQuery("select * from earthquakes_ui where region like '$formattedRegion' order by $orderBy limit $offset, $DEFAULT_PAGE_SIZE")
    }
}
