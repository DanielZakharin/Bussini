package fi.danielz.hslbussin.network.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.danielz.hslbussin.network.apolloClient

@Module
@InstallIn(SingletonComponent::class)
object ApolloClientModule {
    @Provides
    fun provideApolloClient(): ApolloClient = apolloClient
}