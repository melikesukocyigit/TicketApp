package com.turkcell.data.repository

import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.AuthSession
import com.turkcell.core.domain.User
import com.turkcell.core.domain.UserRole
import com.turkcell.data.dto.CredentialsDto
import com.turkcell.data.local.TokenStore
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {

    // Token varsa true, yoksa false döner. UI burayı dinleyip Home sayfasına otomatik geçer.
    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { it != null }

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email = email, password = password))
    }.onSuccess { tokenPairDto ->
        // Başarılı girişte Token'ları cihaza kalıcı olarak kaydet
        tokenStore.save(tokenPairDto.accessToken, tokenPairDto.refreshToken)
    }.map { tokenPairDto ->
        AuthSession(
            user = User(
                id = tokenPairDto.user.id,
                email = tokenPairDto.user.email,
                role = UserRole.fromApi(tokenPairDto.user.role)
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken
        )
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        // Register için Login ile aynı DTO yapısını kullanıyoruz
        authApi.register(CredentialsDto(email = email, password = password))
    }.onSuccess { tokenPairDto ->
        // Kayıt başarılı olduğunda kullanıcıyı direkt içeri almak için token'ı kaydediyoruz
        tokenStore.save(tokenPairDto.accessToken, tokenPairDto.refreshToken)
    }.map { tokenPairDto ->
        AuthSession(
            user = User(
                id = tokenPairDto.user.id,
                email = tokenPairDto.user.email,
                role = UserRole.fromApi(tokenPairDto.user.role)
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken
        )
    }

    override suspend fun logout(): Result<Unit> = runCatchingApi {
        // Çıkış yapıldığında cihazdaki token'ları temizle
        tokenStore.clear()
    }
}