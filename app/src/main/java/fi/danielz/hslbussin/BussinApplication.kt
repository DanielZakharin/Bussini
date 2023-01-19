package fi.danielz.hslbussin

import android.app.Application
import timber.log.Timber

class BussinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}