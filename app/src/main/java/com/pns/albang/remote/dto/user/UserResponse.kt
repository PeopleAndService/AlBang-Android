package com.pns.albang.remote.dto.user

import com.google.gson.annotations.SerializedName
import com.pns.albang.data.User

data class UserResponse(

    @SerializedName("id")
    val userId: Long,

    @SerializedName("googleId")
    val googleId: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("nickname")
    val nickname: String?
) {

    fun toUserModel() = User(this.userId, this.googleId, this.email, this.name, this.nickname)
}
