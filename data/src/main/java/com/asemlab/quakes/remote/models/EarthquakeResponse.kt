package com.asemlab.quakes.remote.models


import androidx.annotation.Keep
import com.asemlab.quakes.database.models.EarthquakeData
import com.google.gson.annotations.SerializedName

@Keep
data class EarthquakeResponse(
    @SerializedName("features")
    val features: List<EarthquakeData>? = null,
    @SerializedName("type")
    val type: String? = null
)