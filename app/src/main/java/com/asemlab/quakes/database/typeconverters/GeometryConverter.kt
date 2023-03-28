package com.asemlab.quakes.database.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.asemlab.quakes.database.models.Geometry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
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
        val type = object : TypeToken<String>() {}.type
        return gson.fromJson(value, type)
    }
}