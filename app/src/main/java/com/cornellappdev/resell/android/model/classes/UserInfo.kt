package com.cornellappdev.resell.android.model.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserInfo(
    val username: String,
    val name: String,
    val netId: String,
    val venmoHandle: String,
    val bio: String,
    val imageUrl: String,
    val id: String,
    val email: String
): Parcelable
