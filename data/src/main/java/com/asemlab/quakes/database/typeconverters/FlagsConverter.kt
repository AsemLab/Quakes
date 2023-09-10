package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.asemlab.quakes.model.Flags
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FlagsConverter {

    @TypeConverter
    fun fromFlags(value: Flags?): String? {
        val gson = Gson()
        val type = object : TypeToken<Flags>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFlags(value: String?): Flags? {
        val gson = Gson()
        val type = object : TypeToken<Flags>() {}.type
        return gson.fromJson(value, type)
    }
}