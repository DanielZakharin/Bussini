package fi.danielz.bussini.complication

import android.content.Context
import androidx.work.*
import timber.log.Timber
import java.time.Duration

class ComplicationDataRefresherWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        if (this.isStopped) return Result.failure()
        Timber.i("ComplicationDataRefresherWorker working to refresh complication data")
        return try {
            requestComplicationUpdate(appContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure(Data.Builder().putAll(mapOf(ERROR_KEY to e)).build())
        }
    }

    companion object {
        const val ERROR_KEY = "ComplicationDataRefresherWorker_ERROR_KEY"
        const val TAG = "ComplicationDataRefresherWorker"
    }
}

fun scheduleComplicationRefreshWork(context: Context, delay: Duration) {
    val tag = ComplicationDataRefresherWorker.TAG
    val wm = WorkManager.getInstance(context)

    // schedule one time work which will trigger refresh of complication and re-trigger this method
    val complicationDataRefreshWorkRequest: OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<ComplicationDataRefresherWorker>()
            .addTag(tag)
            .setInitialDelay(delay)
            .build()

    wm.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, complicationDataRefreshWorkRequest)
}