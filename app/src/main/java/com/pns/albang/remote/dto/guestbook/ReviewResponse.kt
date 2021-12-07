package com.pns.albang.remote.dto.guestbook

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id")
    val guestbookId: Long,

    @SerializedName("content")
    val content: String,

    @SerializedName("anchor")
    val anchor: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("createdTime")
    val createdTime: String,

    @SerializedName("updatedTime")
    val updatedTime: String,

    @SerializedName("userId")
    val userId: Long
)
