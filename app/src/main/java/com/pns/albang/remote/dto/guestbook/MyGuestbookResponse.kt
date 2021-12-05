package com.pns.albang.remote.dto.guestbook

import com.google.gson.annotations.SerializedName

data class MyGuestbookResponse(

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

    @SerializedName("landmarkName")
    val landmarkName: String
)
