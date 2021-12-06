package com.pns.albang.repository

import com.pns.albang.remote.RemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody

object LandmarkRepository {

    private val remoteApiService = RemoteDataSource.apiService

    suspend fun getLandmark() = remoteApiService.getLandmark()
    suspend fun applyLandmark(image: MultipartBody.Part, body: RequestBody) =
        remoteApiService.applyLandmark(image, body)
}