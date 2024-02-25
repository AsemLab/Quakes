package com.asemlab.quakes.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsDatastore private constructor(private var context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("settings")
    }
    private val HAS_LOCATION_REQUESTED = booleanPreferencesKey("has_location_requested")
    suspend fun hasLocationRequested() =
        dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[HAS_LOCATION_REQUESTED] ?: false
            }.first()

    suspend fun setLocationRequested(hasLocationRequested: Boolean) {
        dataStore.edit {
            it[HAS_LOCATION_REQUESTED] = hasLocationRequested
        }
    }

    companion object {
        private var instance: SettingsDatastore? = null

        fun getInstance(context: Context): SettingsDatastore {
            synchronized(this) {
                if (instance == null)
                    instance = SettingsDatastore(context)

                return instance!!
            }
        }
    }


}
