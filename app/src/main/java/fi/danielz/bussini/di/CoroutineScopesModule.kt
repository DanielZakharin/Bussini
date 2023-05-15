package fi.danielz.bussini.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class AppCoroutineScope

@Module
@InstallIn(SingletonComponent::class)
class SingletonCoroutineScopesModule {

    @Singleton
    @Provides
    @AppCoroutineScope
    fun provideApplicationCoroutineScope(
    ): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

}