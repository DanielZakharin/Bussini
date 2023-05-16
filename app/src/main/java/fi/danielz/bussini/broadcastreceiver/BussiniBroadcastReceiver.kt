package fi.danielz.bussini.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fi.danielz.bussini.complication.scheduleComplicationRefreshWork
import timber.log.Timber
import java.time.Duration

class BussiniBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("Bussini received broadcast, updating complication")
        // TODO this should still depend on network
        scheduleComplicationRefreshWork(context, Duration.ZERO)
    }
}