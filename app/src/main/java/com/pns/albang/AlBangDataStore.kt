package com.pns.albang

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(AlBangDataStore.DATA_STORE_NAME)

object AlBangDataStore {

    const val DATA_STORE_NAME = "alBangDataStore"
    val KEY_NAME = stringPreferencesKey("userName")
}