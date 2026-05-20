package com.turkcell.data.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.TicketRepository
import com.turkcell.data.local.TokenStore
import com.turkcell.data.network.AuthInterceptor
import com.turkcell.data.network.TokenAuthenticator
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.remote.TicketApi
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.data.repository.TicketRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

val dataModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { TokenStore(context = get()) }

    single { AuthInterceptor(tokenStore = get()) }

    single {
        TokenAuthenticator(
            tokenStore = get(),
            // HATANIN ÇÖZÜLDÜĞÜ YER: Olmayan REFRESH_API'yi aramak yerine,
            // doğrudan Koin'in bildiği mevcut AuthApi'yi lambda olarak veriyoruz.
            refreshApiProvider = { get() }
        )
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // --- API & REPOSITORY TANIMLAMALARI ---

    single { get<Retrofit>().create(AuthApi::class.java) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            tokenStore = get()
        )
    }

    single { get<Retrofit>().create(TicketApi::class.java) }

    single<TicketRepository> {
        TicketRepositoryImpl(
            ticketApi = get()
        )
    }
}