package com.pns.albang.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val googleId: String,
    val nickname: String?
) : Parcelable
