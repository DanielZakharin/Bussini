package fi.danielz.bussini.network

sealed class NetworkStatus<T> {
    class InProgress<T> : NetworkStatus<T>()

    data class Success<T>(
        val responseBody: T
    ): NetworkStatus<T>() {
        override val body: T = responseBody
    }

    data class Error<T>(
        val exception: Exception
    ): NetworkStatus<T>() {
        override val error: Exception = exception
    }

    open val body: T? = null
    open val error: Exception? = null
}