package com.pns.albang.remote.dto.user

import com.google.gson.annotations.SerializedName

data class ValidateNicknameResponse(

    @SerializedName("result")
    val isDuplicated: Boolean
)
