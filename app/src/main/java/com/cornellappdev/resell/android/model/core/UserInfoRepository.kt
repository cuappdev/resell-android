package com.cornellappdev.resell.android.model.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import com.cornellappdev.resell.android.util.richieUserInfo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val retrofitInstance: RetrofitInstance
) {

    private val userInfo = richieUserInfo

    /**
     * If the user is signed in, returns the user's information. Otherwise, throws an exception.
     *
     * Requires the user to be signed in.
     */
    suspend fun getUserInfo(): UserInfo {
        return userInfo
    }

    suspend fun storeUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }

    suspend fun storeIdToken(idToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ID_TOKEN] = idToken
        }
    }

    suspend fun storeFirstName(firstName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_NAME] = firstName
        }
    }

    suspend fun storeLastName(lastName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_NAME] = lastName
        }
    }

    suspend fun storeProfilePicUrl(profilePicUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_PIC_URL] = profilePicUrl
        }
    }

    suspend fun storeUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun storeAccessToken(token: String) {
        retrofitInstance.updateAccessToken(token)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = token
        }
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }.firstOrNull()
    }

    suspend fun getUsername(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USERNAME]
        }.firstOrNull()
    }

    suspend fun getIdToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ID_TOKEN]
        }.firstOrNull()
    }

    suspend fun getFirstName(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.FIRST_NAME]
        }.firstOrNull()
    }

    suspend fun getLastName(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LAST_NAME]
        }.firstOrNull()
    }

    suspend fun getProfilePicUrl(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.PROFILE_PIC_URL]
        }.firstOrNull()
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }.firstOrNull()
    }
}
