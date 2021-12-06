package com.pns.albang.remote

import com.pns.albang.remote.dto.GetListBasedResponse
import com.pns.albang.remote.dto.guestbook.MyGuestbookResponse
import com.pns.albang.remote.dto.landmark.LandmarkResponse
import com.pns.albang.remote.dto.user.SignInRequest
import com.pns.albang.remote.dto.user.UpdateNicknameRequest
import com.pns.albang.remote.dto.user.UserResponse
import com.pns.albang.remote.dto.user.ValidateNicknameResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // user
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

    @DELETE("/user/withdraw/{id}")
    suspend fun withdraw(
        @Path("id") userId: Long
    ): Response<Void>

    // landmark
    @GET("/landmark/get")
    suspend fun getLandmark(): Response<GetListBasedResponse<LandmarkResponse>>

    // guestbook
    @GET("/guestbook/get/author")
    suspend fun getMyGuestbook(
        @Query("author") userId: Long
    ) : Response<GetListBasedResponse<MyGuestbookResponse>>

    @DELETE("/guestbook/delete/{id}")
    suspend fun deleteGuestbook(
        @Path("id") guestbookId: Long
    ) : Response<Void>

    @Multipart
    @POST("/landmark/application")
    suspend fun applyLandmark(
        @Part image: MultipartBody.Part,
        @Part("body") body: RequestBody
    ) : Response<LandmarkResponse>
}