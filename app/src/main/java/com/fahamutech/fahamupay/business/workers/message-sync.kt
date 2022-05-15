package com.fahamutech.fahamupay.business.workers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.fahamutech.fahamupay.business.models.SendMessageRequest
import com.fahamutech.fahamupay.business.services.getSecretCode
import com.fahamutech.fahamupay.business.services.getServiceCode
import com.fahamutech.fahamupay.business.services.makeMessageSyncRequest
import com.fahamutech.fahamupay.business.services.readAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SyncPaymentMessages(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            if (runAttemptCount > 5) {
                return@withContext Result.failure()
            }
            val messages = readAll(applicationContext)
            val code = getServiceCode(applicationContext)
            val secret = getSecretCode(applicationContext)
            if (code !== null && secret !== null) {
                val r = makeMessageSyncRequest(SendMessageRequest(messages), code, secret)
//                if (r===null)Result.failure()
//                else{
                    Log.e("SERVER RESP", r.size.toString())
                    Result.success()
//                }
            } else {
                Log.e("*** REQUEST ERR", "no code nor secret")
                Result.failure()
            }
        } catch (e: Throwable) {
            Log.e("SYNCS MESSAGES FAIL", e.toString())
            Result.retry()
        }
    }

}

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun periodicSendMessagesWorker(): PeriodicWorkRequest {
    return PeriodicWorkRequestBuilder<SyncPaymentMessages>(
        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
        TimeUnit.MILLISECONDS
    ).setConstraints(constraints).setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS
    ).build()

}

private fun oneTimeSendMessagesWorker(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<SyncPaymentMessages>()
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

fun startSendMessagesWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            "sync_messages_to_fahamupay",
            ExistingWorkPolicy.KEEP,
            oneTimeSendMessagesWorker()
        )
}

fun startPeriodicSendMessagesWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "sync_messages_to_fahamupay_periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSendMessagesWorker()
        )
}

