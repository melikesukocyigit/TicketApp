package com.turkcell.data.util

// tüm api isteklerim tek kalıpta ilerlesin

// .success
// .complete
// .onSuccess
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val error: Throwable) : ApiResult<Nothing>
}

inline fun <T, R> ApiResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R
): R= when(this){
    is ApiResult.Success -> onSuccess(data)
    is ApiResult.Error -> onError(error)
}