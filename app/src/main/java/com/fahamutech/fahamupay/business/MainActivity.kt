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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.fahamutech.fahamupay.business.services.readAll
import com.fahamutech.fahamupay.business.ui.theme.FahamupaybusinessTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FahamupaybusinessTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
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

@Composable
fun Greeting(name: String) {
    val cScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = askPermission(
        onGrant = {
            cScope.launch {
                readAll(context)
            }
        },
        onDenied = {}
    )
    Column {
//        Text(text = "Hello $name!")
        Button(onClick = {
            checkPermissionForReadSMS(context, launcher) {
                cScope.launch {
                    readAll(context)
                }
            }
            checkPermissionForReceiveSMS(context, launcher) {
//                cScope.launch {
//                    readAll(context)
//                }
            }
        }) {
            Text(text = "Hit me")
        }
    }
    LaunchedEffect("app") {
        checkPermissionForReadSMS(context, launcher) {
            cScope.launch {
                readAll(context)
            }
        }
        checkPermissionForReceiveSMS(context, launcher) {
//            cScope.launch {
//                readAll(context)
//            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FahamupaybusinessTheme {
        Greeting("Android")
    }
}