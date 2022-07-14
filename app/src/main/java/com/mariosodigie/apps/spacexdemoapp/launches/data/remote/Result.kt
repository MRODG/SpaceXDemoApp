package com.mariosodigie.apps.spacexdemoapp.launches.data.remote

sealed class Result<T>(val data: T? = null, val message: String? = null, val error: NetworkError? = null) {
    class Success<T>(data:T?): Result<T>(data)
    class Error<T>(error: NetworkError): Result<T>(error = error)
    class Loading<T>(val isLoading: Boolean = true): Result<T>(null)
}