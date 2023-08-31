package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CoordinatesConverter {
    @TypeConverter
    fun fromCoordinates(value: List<Double?>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<Double?>?>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCoordinates(value: String?): List<Double?>? {
        val gson = Gson()
        val type = object : TypeToken<List<Double?>?>() {}.type
        return gson.fromJson(value, type)
    }
}