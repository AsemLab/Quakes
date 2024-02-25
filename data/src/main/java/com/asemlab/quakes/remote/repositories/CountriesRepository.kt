package com.asemlab.quakes.remote.repositories

import android.content.Context
import com.asemlab.quakes.database.CountriesDao
import com.asemlab.quakes.database.typeconverters.CountriesConverter
import com.asemlab.quakes.database.typeconverters.UsaStateConverter
import com.asemlab.quakes.model.CountryData
import com.asemlab.quakes.model.UsaStateData
import com.asemlab.quakes.services.CountriesService
import org.json.JSONArray
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val countriesDao: CountriesDao, private val countriesService: CountriesService
) {

    suspend fun getAllCountries(context: Context): List<CountryData> {
        var countries = countriesDao.getAllCountries()
        if (countries.isEmpty()) {
            insertCountries(context)
            countries = countriesDao.getAllCountries()
        }
        return countries

    }

    suspend fun clearAllCountries() {
        countriesDao.clearCountries()
    }

    private suspend fun insertUsaStates(context: Context) {
        val fileInString: String =
            context.assets.open("usa_states.json").bufferedReader().readText()
        val jsonArray = JSONArray(fileInString)
        val usaStateConverter = UsaStateConverter()
        val l = mutableListOf<UsaStateData>()
        for (i in 0 until jsonArray.length()) {
            usaStateConverter.toUsaState(jsonArray[i].toString())?.let { l.add(it) }
        }

        countriesDao.insertAllStates(l)
    }

    private fun insertCountries(context: Context) {
        val fileInString: String =
            context.assets.open("countries.json").bufferedReader().readText()
        val jsonArray = JSONArray(fileInString)
        val usaStateConverter = CountriesConverter()
        val l = mutableListOf<CountryData>()
        repeat(jsonArray.length() - 1) { i ->
            usaStateConverter.toCountry(jsonArray[i].toString())?.let { l.add(it) }
        }

        countriesDao.insertAll(l)
    }

    suspend fun getUsaStates(context: Context): List<UsaStateData> {
        var states = countriesDao.getAllStates()
        if (states.isEmpty()) {
            insertUsaStates(context)
            states = countriesDao.getAllStates()
        }
        return states
    }

}