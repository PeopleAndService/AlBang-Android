package com.pns.albang.remote.dto.user

import com.google.gson.annotations.SerializedName

data class SignInRequest(

    @SerializedName("googleId")
    val googleId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String
)