package com.pns.albang.remote.dto.guestbook

import com.google.gson.annotations.SerializedName

data class ReviewRequest(

    @SerializedName("content")
    val content: String,

    @SerializedName("anchor")
    val anchor: String,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("landmarkId")
    val landmarkId: Long

)
