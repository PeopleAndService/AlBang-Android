package com.pns.albang.remote.dto.guestbook

import com.google.gson.annotations.SerializedName

data class VanRequest(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("guestbookId")
    val guestbookId: Long
)
