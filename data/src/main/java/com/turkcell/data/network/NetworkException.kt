package com.turkcell.data.network

import android.os.Message
import kotlin.jvm.Throws
//bağlantı kopuk , timeout, dns çözümleme
class NetworkException(cause: Throwable) : RuntimeException("Network Error" , cause)

// Sunucu 4xx, 5xx
class ApiException(
    val code: Int,
    val errorMessage: String?,
    cause: Throwable? = null
): RuntimeException("HTTP $code: $errorMessage" , cause)

{
}