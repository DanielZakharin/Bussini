package fi.danielz.hslbussin.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Provides
import fi.danielz.hslbussin.BuildConfig
import okhttp3.OkHttpClient
import javax.inject.Singleton

private const val apiUrl = "https://api.digitransit.fi/routing/v1/routers/hsl/index/graphql"

private val okHttpClient = OkHttpClient.Builder().build()

val apolloClient = ApolloClient.Builder()
    .okHttpClient(okHttpClient)
    .serverUrl(apiUrl)
    .addHttpHeader("digitransit-subscription-key", BuildConfig.API_KEY)
    .build()
