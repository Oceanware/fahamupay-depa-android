package com.fahamutech.fahamupay.business

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.fahamutech.fahamupay.business.components.CredentialForm
import com.fahamutech.fahamupay.business.services.readAll
import com.fahamutech.fahamupay.business.ui.theme.FahamupaybusinessTheme
import com.fahamutech.fahamupay.business.workers.startPeriodicSendMessagesWorker
import com.fahamutech.fahamupay.business.workers.startSendMessagesWorker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startPeriodicSendMessagesWorker(this)
        setContent {
            FahamupaybusinessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    val context = LocalContext.current
    val cScope = rememberCoroutineScope()
    val workInfo = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData("sync_messages_to_fahamupay")
        .observeAsState()
    val launcher = askPermission(
        onGrant = {
            cScope.launch {
                cScope.launch {
                    startSendMessagesWorker(context)
                }
            }
        },
        onDenied = {}
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = {
            checkPermissionForReadSMS(context, launcher) {
                cScope.launch {
                    startSendMessagesWorker(context)
                }
            }
            checkPermissionForReceiveSMS(context, launcher) {}
        }) {
            Text(text = "Sync messages now.")
        }
        val st: String? = if (workInfo.value?.isEmpty() == true) "PENDING"
        else workInfo.value?.get(0)?.state?.name
        Text(
            text = "STATUS : $st",
            modifier = Modifier.padding(8.dp, 8.dp)
        )
        CredentialForm()
    }
    LaunchedEffect("app") {
        checkPermissionForReadSMS(context, launcher) {
            cScope.launch {
                startSendMessagesWorker(context)
            }
        }
        checkPermissionForReceiveSMS(context, launcher) {}
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FahamupaybusinessTheme {
        Home()
    }
}

@Composable
fun askPermission(
    onGrant: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.e("ExampleScreen", "PERMISSION GRANTED")
            onGrant()
        } else {
            Log.e("ExampleScreen", "PERMISSION DENIED")
            onDenied()
        }
    }
}


fun checkPermissionForReadSMS(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    run: () -> Unit
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) -> {
            Log.e("READ SMS", "Code requires permission")
            run()
        }
        else -> {
            launcher.launch(Manifest.permission.READ_SMS)
        }
    }
}

fun checkPermissionForReceiveSMS(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    run: () -> Unit
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) -> {
            Log.e("RECEIVE SMS", "Code requires permission")
            run()
        }
        else -> {
            launcher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }
}