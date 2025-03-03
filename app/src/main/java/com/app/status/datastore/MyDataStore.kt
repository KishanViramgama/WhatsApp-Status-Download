package com.app.status.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyDataStore @Inject constructor(private val context: Context) {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "status")
        val themSettingKey = stringPreferencesKey("themSetting")
        val isWhatsAppAllow = booleanPreferencesKey("isWhatsAppAllow")
        val isWhatsAppBusinessAllow = booleanPreferencesKey("isWhatsAppBusinessAllow")
        val isDelete = booleanPreferencesKey("isDelete")
    }

    suspend fun setMyDataStoreString(key: Preferences.Key<String>, data: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = data
        }
    }

    fun getMyDataStoreString(
        key: Preferences.Key<String>,
        defaultValue: String = ""
    ): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun setMyDataStoreBoolean(key: Preferences.Key<Boolean>, data: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = data
        }
    }

    fun getMyDataStoreBoolean(
        key: Preferences.Key<Boolean>,
        defaultValue: Boolean = false
    ): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

}