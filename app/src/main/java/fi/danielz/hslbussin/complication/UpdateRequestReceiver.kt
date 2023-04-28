package fi.danielz.hslbussin.complication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class UpdateRequestReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("UpdateReceiver receiver an intent ${intent.toString()}")
        /*
        val result = goAsync()

        val args = intent.extras?

        scope.launch {
            try {
                args.updateState(context)

                // Request an update for the complication that has just been toggled.
                ComplicationDataSourceUpdateRequester
                    .create(
                        context = context,
                        complicationDataSourceComponent = args.providerComponent
                    )
                    .requestUpdate(args.complicationInstanceId)
            } finally {
                // Always call finish, even if cancelled
                result.finish()
            }
        }*/
    }
    companion object {

    }
}
