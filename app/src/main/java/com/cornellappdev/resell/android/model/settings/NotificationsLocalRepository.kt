package com.cornellappdev.resell.android.model.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "notifications")

@Singleton
class NotificationsLocalRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val pauseAllKey = booleanPreferencesKey("pause_all")
    private val chatKey = booleanPreferencesKey("chat")
    private val listingsKey = booleanPreferencesKey("listings")

    val allNotificationsEnabled: StateFlow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[pauseAllKey] ?: true
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    val chatNotificationsEnabled: StateFlow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[chatKey] ?: true
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    val listingsNotificationsEnabled: StateFlow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[listingsKey] ?: true
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[pauseAllKey] = enabled
        }
    }

    suspend fun setChatNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[chatKey] = enabled
        }
    }

    suspend fun setListingsNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[listingsKey] = enabled
        }
    }
}
