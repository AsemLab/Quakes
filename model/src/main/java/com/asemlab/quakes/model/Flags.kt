package com.asemlab.quakes.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Flags(
    @SerializedName("alt")
    val alt: String? = null,
    @SerializedName("png")
    val png: String? = null,
    @SerializedName("svg")
    val svg: String? = null
) : Parcelable