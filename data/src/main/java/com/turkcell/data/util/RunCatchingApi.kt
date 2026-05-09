package com.turkcell.data.util

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException
import retrofit2.HttpException // 10 ay sonra garantisi yok..
import java.io.IOException

// Todo: İlerde tekrar konuşalım.
suspend inline fun <T> runCatchingApi(crossinline block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch(e: HttpException) // HttpException
{
    Result.failure(ApiException(code = e.code(), errorMessage = e.message(), cause=e))
} catch(e: IOException)
{
    Result.failure(NetworkException(e))
} catch(e: Exception)
{
    Result.failure(e)
}

// 1- Bir ekranda koin ile (araştır) authRepository'i kullanmaya çalışalım. (Giriş ekranı)
// backende istek at, cevabı ekrana yazmana gerek yok.