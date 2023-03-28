package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Address(
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("country_code")
    val countryCode: String? = null
)