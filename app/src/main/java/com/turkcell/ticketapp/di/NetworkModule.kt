package com.turkcell.ticketapp.di
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import org.koin.dsl.module
import com.turkcell.data.remote.AuthApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

val networkModule = module {
    single<AuthApi> {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://tickets-api.halitkalayci.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApi::class.java)
    }
}