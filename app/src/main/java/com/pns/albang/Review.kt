package com.pns.albang

data class Review(
    val reviewId: String,
    val reviewContent: String,
    val isMine: Boolean,
    val isReport: Boolean
)
