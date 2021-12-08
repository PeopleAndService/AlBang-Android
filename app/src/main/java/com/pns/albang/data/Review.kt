package com.pns.albang.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val reviewId: Long,
    val reviewContent: String,
    val anchor: String,
    val isMine: Boolean,
    var isReport: Boolean
) : Parcelable
