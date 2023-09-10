package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.asemlab.quakes.model.Geometry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GeometryConverter {

    @TypeConverter
    fun fromGeometry(value: Geometry?): String? {
        val gson = Gson()
        val type = object : TypeToken<Geometry>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGeometry(value: String?): Geometry? {
        val gson = Gson()
        val type = object : TypeToken<Geometry>() {}.type
        return gson.fromJson(value, type)
    }
}