package fi.danielz.hslbussin.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.network.okHttpClient
import dagger.Provides
import fi.danielz.hslbussin.BuildConfig
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

suspend fun <D: Query.Data> ApolloClient.queryAsNetworkResponse(query: Query<D>): NetworkStatus<D> {
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
