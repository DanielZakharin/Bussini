package fi.danielz.hslbussin.network.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.danielz.hslbussin.BuildConfig
import okhttp3.OkHttpClient

const val apiUrl = "https://api.digitransit.fi/routing/v1/routers/hsl/index/graphql"

private val okHttpClient = OkHttpClient.Builder().build()

@Module
@InstallIn(SingletonComponent::class)
object ApolloClientModule {
    @Provides
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .okHttpClient(okHttpClient)
        .serverUrl(apiUrl)
        .addHttpHeader("digitransit-subscription-key", BuildConfig.API_KEY)
        .build()
}