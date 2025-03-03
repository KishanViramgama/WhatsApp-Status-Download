package com.app.status.util

sealed class ResponseData<T>(val data: T? = null, val error: String? = null) {
    class Empty<T> : ResponseData<T>()
    class Loading<T> : ResponseData<T>()
    class Success<T>(data: T? = null) : ResponseData<T>(data = data)
    class Error<T>(data: T? = null, error: String) : ResponseData<T>(data = data, error = error)
    class InternetConnection<T>(error: String) : ResponseData<T>(error = error)
}