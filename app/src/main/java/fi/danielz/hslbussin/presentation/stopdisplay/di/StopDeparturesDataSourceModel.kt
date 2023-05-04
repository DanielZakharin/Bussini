package fi.danielz.hslbussin.presentation.stopdisplay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import fi.danielz.hslbussin.presentation.stopdisplay.model.StopDeparturesDataSource
import fi.danielz.hslbussin.presentation.stopdisplay.model.AutoRefreshingStopDeparturesNetworkDataSource

@Module
@InstallIn(ActivityComponent::class, ViewModelComponent::class)
abstract class StopDeparturesDataSourceModel {
    @Binds
    abstract fun provideStopDeparturesDataSource(stopDeparturesNetworkDataSource: AutoRefreshingStopDeparturesNetworkDataSource): StopDeparturesDataSource
}