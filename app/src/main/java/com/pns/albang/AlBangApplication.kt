package com.pns.albang

import android.app.Application
import com.pns.albang.util.AlBangDataStore

class AlBangApplication : Application() {

    private lateinit var dataStoreInstance: AlBangDataStore

    companion object {
        private lateinit var alBangApplication: AlBangApplication
        fun getApplication() = alBangApplication
    }

    override fun onCreate() {
        super.onCreate()

        alBangApplication = this
        dataStoreInstance = AlBangDataStore(applicationContext)
    }

    fun getDataStore() = dataStoreInstance
}