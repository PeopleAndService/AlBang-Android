package com.pns.albang.remote.dto.landmark

import com.google.gson.annotations.SerializedName

data class LandmarkResponse(

    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("imageName")
    val imageName: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("status")
    val status: String
)
