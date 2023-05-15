package fi.danielz.bussini.di

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import fi.danielz.bussini.preferences.PreferencesManager
import fi.danielz.bussini.preferences.SharedPreferencesManager
import fi.danielz.bussini.utils.getSharedPrefs


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
            fragment.requireContext().getSharedPrefs()
        )
    }
}