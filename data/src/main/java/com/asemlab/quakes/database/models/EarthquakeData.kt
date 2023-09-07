package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "earthquakes")
data class EarthquakeData(
    @PrimaryKey(autoGenerate = true)
    var rowId: Int? = null,
    @SerializedName("geometry")
    var geometry: Geometry? = null,
    @SerializedName("id")
    var serverId: String? = null,
    @SerializedName("properties")
    var properties: Properties? = null
)