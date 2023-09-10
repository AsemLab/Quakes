package com.asemlab.quakes.remote.repositories

import android.content.Context
import com.asemlab.quakes.database.CountriesDao
import com.asemlab.quakes.model.UsaStateData
import com.asemlab.quakes.database.typeconverters.UsaStateConverter
import com.asemlab.quakes.model.CountryData
import com.asemlab.quakes.models.performOnError
import com.asemlab.quakes.models.performOnSuccess
import com.asemlab.quakes.services.CountriesService
import com.asemlab.quakes.utils.performRequest
import org.json.JSONArray
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val countriesDao: CountriesDao, private val countriesService: CountriesService
) {

    suspend fun getAllCountries(
        onError: (String?) -> Unit
    ): List<CountryData> {
        var countries = countriesDao.getAllCountries()
        return countries.ifEmpty {
            val response = performRequest {
                countriesService.getAllCountries()
            }
            response.performOnSuccess {
                countriesDao.insertAll(it)
                countries = it
            }.performOnError {
                onError(it)
            }
            countries
        }

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

    suspend fun getUsaStates(context: Context): List<UsaStateData> {
        var states = countriesDao.getAllStates()
        if (states.isEmpty()) {
            insertUsaStates(context)
            states = countriesDao.getAllStates()
        }
        return states
    }

}