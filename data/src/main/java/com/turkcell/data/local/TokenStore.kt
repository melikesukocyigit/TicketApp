package com.turkcell.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.turkcell.core.domain.auth.User
import com.turkcell.core.domain.auth.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// DataStore kurulumu
private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class TokenStore(private val context: Context) {

    private object Keys {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    val accessToken: Flow<String?> = context.authDataStore.data.map { it[Keys.ACCESS] }
    val refreshToken: Flow<String?> = context.authDataStore.data.map { it[Keys.REFRESH] }

    // ARTIK KİMLİĞİ VE ROLÜ HAFIZADAN OKUYORUZ
    val user: Flow<User?> = context.authDataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID]
        val email = prefs[Keys.USER_EMAIL]
        val role = prefs[Keys.USER_ROLE]

        if (id != null && email != null && role != null) {
            User(id = id, email = email, role = UserRole.fromApi(role))
        } else {
            null
        }
    }

    suspend fun save(access: String, refresh: String, id: String, email: String, role: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            prefs[Keys.REFRESH] = refresh
            prefs[Keys.USER_ID] = id
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_ROLE] = role
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS)
            prefs.remove(Keys.REFRESH)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_ROLE)
        }
    }

    fun accessTokenBlocking(): String? = runBlocking { accessToken.first() }
    fun refreshTokenBlocking(): String? = runBlocking { refreshToken.first() }
    fun clearBlocking() = runBlocking { clear() }

    suspend fun updateTokensOnly(access: String, refresh: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            prefs[Keys.REFRESH] = refresh
        }
    }
    fun saveBlocking(access: String, refresh: String) = runBlocking { updateTokensOnly(access, refresh) }
}