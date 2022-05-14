package com.fahamutech.fahamupay.business.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private fun pref(context: Context): SharedPreferences {
    return context.getSharedPreferences("__fahamupay", Context.MODE_PRIVATE)
}

fun getServiceCode(context: Context): String? {
    return pref(context).getString("CODE",null)
}

fun getSecretCode(context: Context): String? {
    return pref(context).getString("SECRET",null)
}

fun saveServiceCode(code: String, context: Context){
    pref(context).edit {
        putString("CODE", code)
        commit()
    }
}

fun saveServiceSecret(secret: String, context: Context){
    pref(context).edit {
        putString("SECRET", secret)
        commit()
    }
}