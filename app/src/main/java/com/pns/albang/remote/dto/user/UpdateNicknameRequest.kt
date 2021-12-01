package com.pns.albang.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UpdateNicknameRequest(

    @SerializedName("nickname")
    val nickname: String
)
