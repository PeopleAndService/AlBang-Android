package com.pns.albang.repository

import com.pns.albang.remote.RemoteDataSource
import com.pns.albang.remote.dto.user.SignInRequest
import com.pns.albang.remote.dto.user.UpdateNicknameRequest

object UserRepository {

    private val remoteApiService = RemoteDataSource.apiService

    suspend fun signIn(signInRequest: SignInRequest) = remoteApiService.signIn(signInRequest)
    suspend fun checkLogin(userId: Long) = remoteApiService.checkLogin(userId)
    suspend fun validateNickname(nickname: String) = remoteApiService.validateNickname(nickname)
    suspend fun updateNickname(userId: Long, updateNicknameRequest: UpdateNicknameRequest) = remoteApiService.updateNickname(userId, updateNicknameRequest)
}