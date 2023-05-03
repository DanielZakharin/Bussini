package fi.danielz.hslbussin.network

sealed class NetworkStatus<T> {
    class InProgress<T> : NetworkStatus<T>()

    data class Success<T>(
        val responseBody: T
    ): NetworkStatus<T>()

    data class Error<T>(
        val exception: Exception
    ): NetworkStatus<T>()

    // FIXME move inside classes, this is wonky
    val body: T? = when (this) {
        is Success<T> -> responseBody
        else -> null
    }

    // FIXME move inside classes, this is wonky
    val error: Exception? = when (this) {
        is Error<*> -> this.exception
        else -> null
    }
}