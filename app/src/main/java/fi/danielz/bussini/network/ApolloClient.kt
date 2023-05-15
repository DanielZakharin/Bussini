package fi.danielz.bussini.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Attempt to execute Apollo query.
 * @param query ApolloQuery to execute
 * @return result wrapped in [NetworkStatus.Error] or [NetworkStatus.Success]
 */
suspend fun <D : Query.Data> ApolloClient.queryAsNetworkResponse(query: Query<D>): NetworkStatus<D> {
    return try {
        val res = query(query).execute()
        NetworkStatus.Success(
            // TODO how to get actual status code from apollo
            res.dataAssertNoErrors
        )
    } catch (e: Exception) {
        Timber.e(e)
        NetworkStatus.Error(e)
    }
}

/**
 * Attempt to execute Apollo query, return result inside a flow. Starts with [NetworkStatus.InProgress]
 * @param query ApolloQuery to execute
 * @return result wrapped in [NetworkStatus] as flow.
 */
fun <D : Query.Data> ApolloClient.queryAsNetworkResponseFlow(query: Query<D>): Flow<NetworkStatus<D>> {
    return flow {
        emit(NetworkStatus.InProgress())
        emit(queryAsNetworkResponse(query))
    }
}
