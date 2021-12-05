package com.pns.albang.remote.dto

import com.google.gson.annotations.SerializedName

data class GetListBasedResponse<T>(

    @SerializedName("count")
    val count: Int,

    @SerializedName("results")
    val results: List<T>
)
