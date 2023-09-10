package com.asemlab.quakes.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Geometry(
    /**
     * The coordinates are written in latitude and longitude format.
     *
     * coordinates[0] is the longitude
     *
     * coordinates[1] is the latitude
     *
     * coordinates[2] is the depth
     */
    @SerializedName("coordinates")
    val coordinates: List<Double?>? = null
)