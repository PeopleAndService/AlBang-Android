package com.pns.albang.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AlBangDataStore(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)

    companion object {
        private const val DATA_STORE_NAME = "alBangDataStore"

        private const val DEFAULT_INT_VALUE = 0
        private const val DEFAULT_LONG_VALUE = 0L
        private const val DEFAULT_DOUBLE_VALUE = 0.0
        private const val DEFAULT_BOOLEAN_VALUE = false
        private const val DEFAULT_STRING_VALUE = ""
    }

    private fun getIntKey(name: String) = intPreferencesKey(name)

    private fun getLongKey(name: String) = longPreferencesKey(name)

    private fun getDoubleKey(name: String) = doublePreferencesKey(name)

    private fun getBooleanKey(name: String) = booleanPreferencesKey(name)

    private fun getStringKey(name: String) = stringPreferencesKey(name)

    suspend fun setValue(key: String, value: Any) {
        context.dataStore.edit { prefs ->
            when (value) {
                is Int -> prefs[getIntKey(key)] = value
                is Long -> prefs[getLongKey(key)] = value
                is Double -> prefs[getDoubleKey(key)] = value
                is Boolean -> prefs[getBooleanKey(key)] = value
                is String -> prefs[getStringKey(key)] = value
            }
        }
    }

    fun getIntValue(key: String) = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[getIntKey(key)] ?: DEFAULT_INT_VALUE
        }

    fun getLongValue(key: String) = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[getLongKey(key)] ?: DEFAULT_LONG_VALUE
        }

    fun getDoubleValue(key: String) = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[getDoubleKey(key)] ?: DEFAULT_DOUBLE_VALUE
        }

    fun getBooleanValue(key: String) = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[getBooleanKey(key)] ?: DEFAULT_BOOLEAN_VALUE
        }

    fun getStringValue(key: String) = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[getStringKey(key)] ?: DEFAULT_STRING_VALUE
        }

    suspend fun removeIntKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(getIntKey(key))
        }
    }

    suspend fun removeLongKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(getLongKey(key))
        }
    }

    suspend fun removeDoubleKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(getDoubleKey(key))
        }
    }

    suspend fun removeBooleanKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(getBooleanKey(key))
        }
    }

    suspend fun removeStringKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(getStringKey(key))
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}