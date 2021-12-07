package com.pns.albang.remote.dto.guestbook

import com.google.gson.annotations.SerializedName

data class VanRequest(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("guestBookId")
    val guestbookId: Long
)
