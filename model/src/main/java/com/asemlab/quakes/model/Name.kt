package com.asemlab.quakes.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Name(
    @SerializedName("common")
    val common: String? = null
)