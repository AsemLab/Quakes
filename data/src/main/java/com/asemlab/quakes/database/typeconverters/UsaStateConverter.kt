package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.asemlab.quakes.model.UsaStateData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UsaStateConverter {

    @TypeConverter
    fun fromUsaState(value: UsaStateData?): String?{
        val gson = Gson()
        val type = object: TypeToken<UsaStateData>(){}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toUsaState(value: String?): UsaStateData?{
        val gson = Gson()
        val type = object: TypeToken<UsaStateData>(){}.type
        return gson.fromJson(value, type)
    }


}