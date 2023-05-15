package fi.danielz.bussini.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloException

suspend fun <D : Query.Data> ApolloClient.makeRequest(
    query: Query<D>,
    onError: (ApolloException?, List<Error>?) -> Unit,
    onSuccess: (D?) -> Unit
) {
    try {
        val response = query(query).execute()
        response.data?.let {
            onSuccess(it)
        } ?: response.errors?.let {
            onError(null, it)
        }
    } catch (e: ApolloException) {
        onError(e, null)
    }
}

data class NetworkOrApolloError(
    val apolloErrors: List<Error>?,
    val networkError: ApolloException?
)

/*sealed class ApolloResult<T, AP: Operation.Data>(apolloResponse: AP) {
    abstract val data: T?
    abstract val error: NetworkOrApolloError?
    class Success<T, AP: Operation>(apolloResponse: ApolloResponse<AP>): ApolloResult<T>(apolloResponse) {
        override val data: T? = apolloResponse.data
        override val error: NetworkOrApolloError?
            get() = TODO("Not yet implemented")

    }
}*/