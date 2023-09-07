package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "countries")
data class CountryData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @SerializedName("cca2")
    var cca2: String? = null,
    @SerializedName("flags")
    var flags: Flags? = null,
    @SerializedName("name")
    var name: Name? = null,
    @SerializedName("region")
    var region: String? = null
)