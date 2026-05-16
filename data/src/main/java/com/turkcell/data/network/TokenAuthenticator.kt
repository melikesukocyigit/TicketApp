package com.turkcell.data.network

import com.turkcell.data.dto.RefreshRequestDto
import com.turkcell.data.local.TokenStore
import com.turkcell.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApiProvider: () -> AuthApi,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // İsteğin tekrar tekrar buraya düşmesi -> refresh olsa bile 401 gelebilir
        if(response.priorResponseCount() >= 1) return null

        val refreshToken = tokenStore.refreshTokenBlocking() ?: return null;

        return synchronized(this)
        {
            val current = tokenStore.accessTokenBlocking()
            val sentToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            if(current != null && current != sentToken){
                return@synchronized response.request.signWith(current)
            }

            val newPair = runCatching {
                runBlocking { refreshApiProvider().refresh(RefreshRequestDto(refreshToken)) }
            }.getOrNull()

            if(newPair==null){
                tokenStore.clearBlocking()
                return@synchronized null
            }

            tokenStore.saveBlocking(newPair.accessToken, newPair.refreshToken)
            response.request.signWith(newPair.accessToken)
        }
    }

    private fun Request.signWith(accessToken: String): Request = newBuilder().header("Authorization", "Bearer $accessToken").build()

    private fun Response.priorResponseCount() : Int{
        var count = 0
        var prior = priorResponse
        while(prior != null)
        {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

// Ana sayfada
// etkinlikler ve "biletlerim" diye 2 tane tasarım
// apiden etkinlikleri ve biletleri çekip gösterecek..