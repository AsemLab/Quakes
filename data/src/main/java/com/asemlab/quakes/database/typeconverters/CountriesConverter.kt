package com.asemlab.quakes.database.typeconverters

import androidx.room.TypeConverter
import com.asemlab.quakes.model.CountryData
import com.asemlab.quakes.model.UsaStateData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CountriesConverter {

    @TypeConverter
    fun fromCountry(value: CountryData?): String?{
        val gson = Gson()
        val type = object: TypeToken<CountryData>(){}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountry(value: String?): CountryData?{
        val gson = Gson()
        val type = object: TypeToken<CountryData>(){}.type
        return gson.fromJson(value, type)
    }


}