package com.pns.albang.repository

import com.pns.albang.remote.RemoteDataSource

object LandmarkRepository {

    private val remoteApiService = RemoteDataSource.apiService

    suspend fun getLandmark() = remoteApiService.getLandmark()
}