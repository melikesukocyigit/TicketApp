package com.turkcell.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.turkcell.core.domain.auth.User
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
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // Token akışları
    val accessToken: Flow<String?> = context.authDataStore.data.map { it[Keys.ACCESS] }
    val refreshToken: Flow<String?> = context.authDataStore.data.map { it[Keys.REFRESH] }

    // Profesyonel rol kontrolü için kullanıcı akışı
    val user: Flow<User?> = context.authDataStore.data.map { prefs ->
        val email = prefs[Keys.USER_EMAIL]
        val role = prefs[Keys.USER_ROLE]

        if (email != null && role != null) {
            // User(id = "...", email = email, role = UserRole.fromApi(role))
            null
        } else {
            null
        }
    }

    suspend fun save(access: String, refresh: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            prefs[Keys.REFRESH] = refresh
        }
    }

    suspend fun clear() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS)
            prefs.remove(Keys.REFRESH)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_ROLE)
        }
    }

    // Bloklayan yardımcı metotlar (Test ve senkron işlemler için)
    fun accessTokenBlocking(): String? = runBlocking { accessToken.first() }
    fun refreshTokenBlocking(): String? = runBlocking { refreshToken.first() }
    fun saveBlocking(access: String, refresh: String) = runBlocking { save(access, refresh) }
    fun clearBlocking() = runBlocking { clear() }
}