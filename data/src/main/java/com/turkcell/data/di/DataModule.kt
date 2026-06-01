package com.turkcell.data.di

import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.checkin.CheckInRepository
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.TicketRepository
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.data.local.TokenStore
import com.turkcell.data.network.AuthInterceptor
import com.turkcell.data.network.TokenAuthenticator
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.remote.CheckInApi
import com.turkcell.data.remote.EventApi
import com.turkcell.data.remote.MeApi
import com.turkcell.data.remote.PurchaseApi
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.data.repository.CheckInRepositoryImpl
import com.turkcell.data.repository.EventRepositoryImpl
import com.turkcell.data.repository.PurchaseRepositoryImpl
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

    // 1. JSON Ayarları
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }

    // 2. Token ve Interceptor'lar
    single { TokenStore(context = get()) }

    single { AuthInterceptor(tokenStore = get()) }

    single {
        TokenAuthenticator(
            tokenStore = get(),
            refreshApiProvider = { get() }
        )
    }

    // 3. OkHttp ve Retrofit
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
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

    // 4. API ve Repository Tanımlamaları
    single { get<Retrofit>().create(AuthApi::class.java) }

    single { get<Retrofit>().create(EventApi::class.java) }

    single { get<Retrofit>().create(PurchaseApi::class.java) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            tokenStore = get()
        )
    }

    single<EventRepository> {
        EventRepositoryImpl(
            eventApi = get()
        )
    }


    single<PurchaseRepository> {
        PurchaseRepositoryImpl(
            purchaseApi = get()
        )
    }

    single { get<Retrofit>().create(CheckInApi::class.java) }

    single<CheckInRepository> {
        CheckInRepositoryImpl(checkInApi = get())
    }
    single { get<Retrofit>().create(MeApi::class.java) }

    single<TicketRepository> {
        TicketRepositoryImpl(meApi = get())
    }
}

