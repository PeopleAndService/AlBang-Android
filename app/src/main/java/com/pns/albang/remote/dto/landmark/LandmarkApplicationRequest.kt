package com.pns.albang.remote.dto.landmark

import com.google.gson.annotations.SerializedName

data class LandmarkApplicationRequest(

    @SerializedName("name")
    val name: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String
)
