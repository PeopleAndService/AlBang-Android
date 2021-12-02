package com.pns.albang.remote.dto.landmark

import com.google.gson.annotations.SerializedName
import com.pns.albang.data.Landmark

data class AllLandmarkResponse(

    @SerializedName("count")
    val count: Int,

    @SerializedName("result")
    val result: List<LandmarkResponse>
)

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
