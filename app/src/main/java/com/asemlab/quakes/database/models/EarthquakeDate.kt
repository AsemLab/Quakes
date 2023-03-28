package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "earthquakes")
data class EarthquakeDate(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @SerializedName("geometry")
    val geometry: Geometry? = null,
    @SerializedName("id")
    val serverId: String? = null,
    @SerializedName("properties")
    val properties: Properties? = null
)