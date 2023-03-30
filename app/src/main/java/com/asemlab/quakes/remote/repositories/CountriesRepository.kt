package com.asemlab.quakes.remote.repositories

import android.content.Context
import com.asemlab.quakes.database.CountriesDao
import com.asemlab.quakes.database.models.CountryData
import com.asemlab.quakes.database.models.UsaStateData
import com.asemlab.quakes.database.typeconverters.UsaStateConverter
import com.asemlab.quakes.remote.performOnError
import com.asemlab.quakes.remote.performOnSuccess
import com.asemlab.quakes.remote.services.CountriesService
import com.asemlab.quakes.utils.performRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.json.JSONArray
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val countriesDao: CountriesDao, private val countriesService: CountriesService
) {

    suspend fun getAllCountries(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<List<CountryData>> {
        return flow {
            val countries = countriesDao.getAllCountries()
            if (countries.isEmpty()) {
                val response = performRequest {
                    countriesService.getAllCountries()
                }
                response.performOnSuccess {
                    countriesDao.insertAll(it)
                    emit(it)
                }.performOnError {
                    onError(it)
                }
            } else {
                emit(countries)
            }
        }.onStart { onStart() }.onCompletion { onComplete() }
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