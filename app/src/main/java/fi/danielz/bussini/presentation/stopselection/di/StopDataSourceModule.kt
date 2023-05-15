package fi.danielz.bussini.presentation.stopselection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import fi.danielz.bussini.presentation.stopselection.model.StopsDataSource
import fi.danielz.bussini.presentation.stopselection.model.StopsNetworkDataSource

@Module
@InstallIn(ActivityComponent::class, ViewModelComponent::class)
abstract class StopDataSourceModule {
    @Binds
    abstract fun provideStopsDataSource(stopsNetworkDataSource: StopsNetworkDataSource): StopsDataSource
}