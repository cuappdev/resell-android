package com.cornellappdev.resell.android.model.classes.login

data class FirebaseUser(
    val venmo: String,
    val onboarded: Boolean,
    val notificationsEnabled: Boolean,
    val fcmToken: String
)
