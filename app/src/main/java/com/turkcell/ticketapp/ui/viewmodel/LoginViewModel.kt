package com.turkcell.ticketapp.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    // Ekranda gösterilecek sonucun state'i
    var resultText by mutableStateOf("Henüz istek atılmadı.")
        private set

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            resultText = "İstek atılıyor, lütfen bekleyin..."

            // Result yapısı ile dönen cevabı işliyoruz
            repository.login(email, pass).fold(
                onSuccess = { session ->
                    resultText = "Giriş Başarılı!\nRol: ${session.user.role}\nToken: ${session.accessToken.take(15)}..."
                },
                onFailure = { error ->
                    resultText = "Hata Oluştu: ${error.message}"
                }
            )
        }
    }
}