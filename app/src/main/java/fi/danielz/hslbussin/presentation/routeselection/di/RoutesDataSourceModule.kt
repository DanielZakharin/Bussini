package fi.danielz.hslbussin.presentation.routeselection.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesNetworkDataSource
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Module
@InstallIn(SingletonComponent::class)
abstract class RoutesDataSourceModule {
    @Binds
    @Singleton
    abstract fun provideRoutesDataSource(routesNetworkDataSource: RoutesNetworkDataSource): RoutesDataSource

}