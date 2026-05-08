package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.ui.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
}