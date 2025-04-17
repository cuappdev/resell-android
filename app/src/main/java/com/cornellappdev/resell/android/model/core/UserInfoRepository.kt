package com.cornellappdev.resell.android.model.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.model.classes.UserInfo
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.PreferencesKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private var accessToken: String? = null

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
        val venmoHandleDeferred = async { getVenmoHandle()!! }
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

    suspend fun storeUserFromUserObject(user: User) = coroutineScope {
        // Launch async calls for each of the repository methods
        val storeUserId = async { storeUserId(user.id) }
        val storeBio = async { storeBio(user.bio) }
        val storeNetId = async { storeNetId(user.netid) }
        val storeEmail = async { storeEmail(user.email) }
        val storeUsername = async { storeUsername(user.username) }
        val storeFirstName = async { storeFirstName(user.givenName) }
        val storeLastName = async { storeLastName(user.familyName) }
        val storeProfilePicUrl = async { storeProfilePicUrl(user.photoUrl) }
        val storeVenmoHandle = async { storeVenmoHandle(user.venmoHandle ?: "") }

        awaitAll(
            storeUserId,
            storeBio,
            storeNetId,
            storeEmail,
            storeUsername,
            storeFirstName,
            storeLastName,
            storeProfilePicUrl,
            storeVenmoHandle
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
     * Stores the FIREBASE access token to userInfoRepository.
     */
    suspend fun storeAccessToken(token: String) {
        accessToken = token
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = token
        }
    }

    suspend fun storeVenmoHandle(venmoHandle: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VENMO_HANDLE] = venmoHandle
        }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }.firstOrNull()
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

    suspend fun getVenmoHandle(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.VENMO_HANDLE]
        }.firstOrNull()
    }
}
