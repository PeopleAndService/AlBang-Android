package com.pns.albang.data

data class User(

    val userId: Long,
    val googleId: String,
    val email: String,
    val name: String,
    val nickname: String?
)
