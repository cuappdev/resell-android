package com.cornellappdev.resell.android.model.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

object PreferencesKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val ID_TOKEN = stringPreferencesKey("id_token")
    val USERNAME = stringPreferencesKey("username")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val FIRST_NAME = stringPreferencesKey("first_name")
    val LAST_NAME = stringPreferencesKey("last_name")
    val PROFILE_PIC_URL = stringPreferencesKey("profile_pic_url")
    val NET_ID = stringPreferencesKey("net_id")
    val BIO = stringPreferencesKey("bio")
    val EMAIL = stringPreferencesKey("email")
    val GCAL_SYNC = stringPreferencesKey("gcal_sync")
    val VENMO_HANDLE = stringPreferencesKey("venmo_handle")
    val HIDDEN_SEARCHES = stringPreferencesKey("hidden_searches")
}

@Module
@InstallIn(SingletonComponent::class) // This ensures it's a singleton across the app
object LoginStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
