package fi.danielz.bussini.presentation.routeselection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.danielz.bussini.presentation.routeselection.model.RoutesDataSource
import fi.danielz.bussini.presentation.routeselection.model.RoutesNetworkDataSource
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RoutesDataSourceModule {
    @Binds
    @Singleton
    abstract fun provideRoutesDataSource(routesNetworkDataSource: RoutesNetworkDataSource): RoutesDataSource

}