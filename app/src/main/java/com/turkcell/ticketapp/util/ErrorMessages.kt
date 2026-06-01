package com.turkcell.ticketapp.util

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException

fun Throwable.toUserMessage(): String = when(this) {
    is ApiException -> when(code) {
        401 -> "Email veya şifre hatalı"
        409 -> {
            if (message?.contains("email_taken") == true) "Bu email zaten kayıtlı"
            else if (message?.contains("capacity_exceeded") == true) "Stok yetersiz, yenile"
            else if (message?.contains("already_paid") == true) "Bu bilet zaten ödenmiş"
            else "Geçersiz işlem (409)"
        }
        403 -> {
            if (message?.contains("not_purchase_owner") == true) "Bu işlemi yapmaya yetkiniz yok"
            else "Yetkisiz işlem"
        }
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu."
}