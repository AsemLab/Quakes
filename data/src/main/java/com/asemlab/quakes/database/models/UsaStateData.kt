package com.asemlab.quakes.database.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "usa_states")
data class UsaStateData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("name")
    var name: String? = null
)