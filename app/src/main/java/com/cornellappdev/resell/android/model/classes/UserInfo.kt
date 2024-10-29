package com.cornellappdev.resell.android.model.classes

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val username: String,
    val name: String,
    val netId: String,
    val venmoHandle: String,
    val bio: String,
    val imageUrl: String,
    val id: String
)
