package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GeoLocationData(
    @SerializedName("address")
    val address: Address? = null,
    @SerializedName("display_name")
    val displayName: String? = null
)