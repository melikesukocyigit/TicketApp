package com.turkcell.ticketapp.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.repository.AuthRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}