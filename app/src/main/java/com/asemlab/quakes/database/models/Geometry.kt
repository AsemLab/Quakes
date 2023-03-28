package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<Double?>? = null
)