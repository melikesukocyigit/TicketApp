package com.turkcell.ticketapp

import android.app.Application
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.ticketapp.di.networkModule
import com.turkcell.ticketapp.di.repositoryModule
import com.turkcell.ticketapp.di.viewModelModule
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import com.turkcell.ticketapp.ui.viewmodel.LoginViewModel

class TicketAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TicketAppApplication)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }
    }
}