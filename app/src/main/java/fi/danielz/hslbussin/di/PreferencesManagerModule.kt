package fi.danielz.hslbussin.di

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import fi.danielz.hslbussin.preferences.PreferencesManager
import fi.danielz.hslbussin.preferences.SharedPreferencesManager


@Module
@InstallIn(FragmentComponent::class)
interface PreferencesManagerModule {
    @Binds
    fun bindPreferencesManager(shared: SharedPreferencesManager): PreferencesManager
}

@Module
@InstallIn(FragmentComponent::class)
class SharedPreferencesManagerModule {

    @Provides
    fun provideSharePreferencesManager(fragment: Fragment): SharedPreferencesManager {
        return SharedPreferencesManager(
            fragment.requireActivity().getPreferences(Context.MODE_PRIVATE)
        )
    }
}