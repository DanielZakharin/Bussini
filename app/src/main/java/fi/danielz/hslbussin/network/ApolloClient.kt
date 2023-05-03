package fi.danielz.hslbussin.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

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

suspend fun <D : Query.Data> ApolloClient.queryAsNetworkResponseFlow(query: Query<D>): Flow<NetworkStatus<D>> {
    return flow {
        emit(NetworkStatus.InProgress())
        emit(queryAsNetworkResponse(query))
    }
}
