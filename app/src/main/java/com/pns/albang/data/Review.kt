package com.pns.albang.data

data class Review(
    val reviewId: Long,
    val reviewContent: String,
    val anchor: String,
    val isMine: Boolean,
    val isReport: Boolean
)
