package com.pns.albang.remote

import com.pns.albang.remote.dto.user.SignInRequest
import com.pns.albang.remote.dto.user.UpdateNicknameRequest
import com.pns.albang.remote.dto.user.UserResponse
import com.pns.albang.remote.dto.user.ValidateNicknameResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/user/new")
    suspend fun signIn(
        @Body signInRequestBody: SignInRequest
    ): Response<UserResponse>

    @GET("user/login/{id}")
    suspend fun checkLogin(
        @Path("id") userId: Long
    ): Response<UserResponse>

    @GET("/user")
    suspend fun validateNickname(
        @Query("nickname") nickname: String
    ): Response<ValidateNicknameResponse>

    @PATCH("/user/nickname/{id}")
    suspend fun updateNickname(
        @Path("id") userId: Long,
        @Body updateNicknameRequest: UpdateNicknameRequest
    ): Response<UserResponse>
}