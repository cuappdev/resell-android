package com.cornellappdev.resell.android.model.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val retrofitInstance: RetrofitInstance,
    private val fireStoreRepository: FireStoreRepository,
) {

    /**
     * If the user is signed in, returns the user's information. Otherwise, throws an exception.
     *
     * Requires the user to be signed in.
     */
    suspend fun getUserInfo(): UserInfo = coroutineScope {
        // Load in parallel :-)
        val usernameDeferred = async { getUsername()!! }
        val firstNameDeferred = async { getFirstName() }
        val lastNameDeferred = async { getLastName() }
        val imageUrlDeferred = async { getProfilePicUrl()!! }
        val netIdDeferred = async { getNetId()!! }
        val venmoHandleDeferred = async { fireStoreRepository.getVenmoHandle(getEmail()!!) }
        val bioDeferred = async { getBio()!! }
        val userIdDeferred = async { getUserId()!! }
        val emailDeferred = async { getEmail()!! }

        return@coroutineScope UserInfo(
            username = usernameDeferred.await(),
            name = "${firstNameDeferred.await()} ${lastNameDeferred.await()}",
            imageUrl = imageUrlDeferred.await(),
            netId = netIdDeferred.await(),
            venmoHandle = venmoHandleDeferred.await(),
            bio = bioDeferred.await(),
            id = userIdDeferred.await(),
            email = emailDeferred.await()
        )
    }

    suspend fun storeUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }

    suspend fun storeEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL] = email
        }
    }

    suspend fun storeNetId(netId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NET_ID] = netId
        }
    }

    suspend fun storeBio(bio: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIO] = bio
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

    /**
     * Stores the FIREBASE access token.
     */
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

    suspend fun getBio(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.BIO]
        }.firstOrNull()
    }

    suspend fun getNetId(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.NET_ID]
        }.firstOrNull()
    }

    suspend fun getEmail(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.EMAIL]
        }.firstOrNull()
    }
}
