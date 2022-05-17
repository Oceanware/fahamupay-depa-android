package com.fahamutech.fahamupay.business.services

import android.content.Context
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList

suspend fun readAll(context: Context, remoteHashes: Array<String>): ArrayList<String> {
    val messages = arrayListOf<String>()
    withContext(Dispatchers.IO) {
        try{
            val projection = arrayOf(
                Telephony.TextBasedSmsColumns.READ,
                Telephony.TextBasedSmsColumns.BODY,
                Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.THREAD_ID,
            )
            val cursor = context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                projection,
                "${Telephony.TextBasedSmsColumns.ADDRESS} = ?",
                arrayOf("TIGOPESA"),
                Telephony.Sms.DEFAULT_SORT_ORDER
            )
            when (cursor?.count) {
                null -> {
                    Log.e("READ SMS","******ERROR")
                }
                0 -> {
                    Log.e("READ SMS","******NOTHING FOUND")
                }
                else -> {
                    cursor.apply {
                        val bodyIndex: Int = getColumnIndex(Telephony.TextBasedSmsColumns.BODY)
                        while (moveToNext()) {
                            val body = getString(bodyIndex)
                            val isNew = remoteHashes.toList().contains(sha1(body)).not()
                                .and(body.lowercase(Locale.ROOT).contains("umepokea"))
                            if (isNew) {
//                            Log.e("ROW SMS", "address: $address, body: $body")
                                messages.add(body)
                            }
                        }
                    }
                }
            }
        }catch (e:Throwable){
            e.message?.let { Log.e("READ SMS", it) }
            Toast.makeText(context,e.message, Toast.LENGTH_SHORT).show()
        }
    }
    return messages
}

fun sha1(data: String): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val hashInByte = digest.digest(data.toByteArray())
    val sb = StringBuilder()
    for (b in hashInByte) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}