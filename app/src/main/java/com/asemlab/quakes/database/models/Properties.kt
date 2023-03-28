package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Properties(
    @SerializedName("felt")
    val felt: Int? = null,
    @SerializedName("mag")
    val mag: Double? = null,
    @SerializedName("place")
    val place: String? = null,
    @SerializedName("time")
    val time: Long? = null,
    @SerializedName("updated")
    val updated: Long? = null
)