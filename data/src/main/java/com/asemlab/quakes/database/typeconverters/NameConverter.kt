package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.asemlab.quakes.database.models.Name
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NameConverter {

    @TypeConverter
    fun fromName(value: Name?): String? {
        val gson = Gson()
        val type = object : TypeToken<Name>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toName(value: String?): Name? {
        val gson = Gson()
        val type = object : TypeToken<Name>() {}.type
        return gson.fromJson(value, type)
    }
}