package com.pns.albang.repository

import com.pns.albang.remote.RemoteDataSource

object MyGuestbookRepository {

    private val remoteApiService = RemoteDataSource.apiService

    suspend fun getMyGuestbook(userId: Long) = remoteApiService.getMyGuestbook(userId)
    suspend fun deleteMyGuestbook(guestbookId: Long) = remoteApiService.deleteGuestbook(guestbookId)
}