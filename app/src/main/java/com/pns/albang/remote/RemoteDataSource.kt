package com.pns.albang.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteDataSource {

    private const val ALBANG_WAS_URL = "http://203.255.3.231:1130"

    private val mGson = GsonBuilder().setLenient().create()

    private val mRetrofit = Retrofit.Builder()
        .baseUrl(ALBANG_WAS_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(mGson))
        .build()

    val apiService = mRetrofit.create(ApiService::class.java)
}