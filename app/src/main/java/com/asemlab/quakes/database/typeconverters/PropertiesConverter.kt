package com.asemlab.quakes.database.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.asemlab.quakes.database.models.Properties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class PropertiesConverter {

    @TypeConverter
    fun fromProperties(value: Properties?): String? {
        val gson = Gson()
        val type = object : TypeToken<Properties>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toProperties(value: String?): Properties? {
        val gson = Gson()
        val type = object : TypeToken<String>() {}.type
        return gson.fromJson(value, type)
    }
}