package com.pns.albang.data

data class MyGuestbook(

    val guestbookId: Long,
    val content: String,
    val state: String,
    val createdTime: String,
    val updatedTime: String,
    val landmarkName: String
)
