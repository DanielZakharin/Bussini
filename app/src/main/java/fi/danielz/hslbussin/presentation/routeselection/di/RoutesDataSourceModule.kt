package fi.danielz.hslbussin.presentation.routeselection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesNetworkDataSource


@Module
@InstallIn(ActivityComponent::class, ViewModelComponent::class)
abstract class RoutesDataSourceModule {
    @Binds
    abstract fun provideRoutesDataSource(routesNetworkDataSource: RoutesNetworkDataSource): RoutesDataSource

}