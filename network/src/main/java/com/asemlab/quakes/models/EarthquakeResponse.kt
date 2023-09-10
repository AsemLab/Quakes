package com.asemlab.quakes.models


import androidx.annotation.Keep
import com.asemlab.quakes.model.EarthquakeData
import com.google.gson.annotations.SerializedName

@Keep
data class EarthquakeResponse(
    @SerializedName("features")
    val features: List<EarthquakeData>? = null,
    @SerializedName("type")
    val type: String? = null
)