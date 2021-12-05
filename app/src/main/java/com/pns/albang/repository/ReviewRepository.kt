package com.pns.albang.repository

import com.pns.albang.remote.RemoteDataSource
import com.pns.albang.remote.dto.guestbook.ReviewRequest
import com.pns.albang.remote.dto.guestbook.VanRequest

object ReviewRepository {

    private val remoteApiService = RemoteDataSource.apiService

    suspend fun setReview(reviewRequest: ReviewRequest) = remoteApiService.setReview(reviewRequest)
    suspend fun getReviews(landmarkId: Long) = remoteApiService.getReviews(landmarkId)
    suspend fun requestVan(vanRequest: VanRequest) = remoteApiService.requestVan(vanRequest)
    suspend fun deleteReview(guestbookId: Long) = remoteApiService.deleteGuestbook(guestbookId)
}