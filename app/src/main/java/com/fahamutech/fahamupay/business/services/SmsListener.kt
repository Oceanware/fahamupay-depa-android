package com.fahamutech.fahamupay.business.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fahamutech.fahamupay.business.workers.startSendMessagesWorker
import kotlinx.coroutines.*

/**
 * Run work asynchronously from a [BroadcastReceiver].
 */
fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch(dispatcher) {
        block()
        pendingResult.finish()
    }
}

class SmsListener: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG****","RECEIVE SIGNAL")
        if(context!=null)startSendMessagesWorker(context)
    }
}