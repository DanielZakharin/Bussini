package fi.danielz.hslbussin.network

sealed class NetworkStatus<T> {
    class InProgress<T> : NetworkStatus<T>()

    data class Success<T>(
        val responseBody: T
    ): NetworkStatus<T>()

    data class Error<T>(
        val error: Any
    ): NetworkStatus<T>()

    val body: T? = when (this) {
        is Success<T> -> responseBody
        else -> null
    }
}