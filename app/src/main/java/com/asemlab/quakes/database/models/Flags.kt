package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Flags(
    @SerializedName("alt")
    val alt: String? = null,
    @SerializedName("png")
    val png: String? = null,
    @SerializedName("svg")
    val svg: String? = null
)