package com.fahamutech.fahamupay.business.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fahamutech.fahamupay.business.R
import com.fahamutech.fahamupay.business.services.getSecretCode
import com.fahamutech.fahamupay.business.services.getServiceCode
import com.fahamutech.fahamupay.business.services.saveServiceCode
import com.fahamutech.fahamupay.business.services.saveServiceSecret

//@Composable
//private fun CodeInput(code: String){
//    TextField(value = code, onValueChange = {
//
//    } )
//}

@Composable
fun CredentialForm() {
    val context = LocalContext.current
    var code by remember {
        mutableStateOf("")
    }
    var secret by remember {
        mutableStateOf("")
    }
    var editCode by remember {
        mutableStateOf(false)
    }
    var editSecret by remember {
        mutableStateOf(false)
    }
    Column {
        Text(
            text = "Add your client configuration",
            modifier = Modifier.padding(8.dp, 8.dp),
            textAlign = TextAlign.Justify
        )
        TextField(
            value = code,
            onValueChange = {
                code = it
                saveServiceCode(it, context)
            },
            placeholder = {
                Text(text = "Code")
            },
            label = {
                Text(text = "Code")
            },
            modifier = Modifier.padding(8.dp, 8.dp),
            trailingIcon = {
                if (editCode) {
                    IconButton(onClick = { editCode = false }) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    }
                } else {
                    IconButton(onClick = { editCode = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "")
                    }
                }
            },
            readOnly = !editCode
        )

        TextField(
            value = secret,
            onValueChange = {
                secret = it
                saveServiceSecret(it, context)
            },
            placeholder = {
                Text(text = "Secret")
            },
            label = {
                Text(text = "Secret")
            },
            modifier = Modifier.padding(8.dp, 8.dp),
            trailingIcon = {
                if (editSecret) {
                    IconButton(onClick = { editSecret = false }) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    }
                } else {
                    IconButton(onClick = { editSecret = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "")
                    }
                }
            },
            readOnly = !editSecret
        )
    }
    LaunchedEffect("form") {
        code = getServiceCode(context) ?: ""
        secret = getSecretCode(context) ?: ""
    }
}
