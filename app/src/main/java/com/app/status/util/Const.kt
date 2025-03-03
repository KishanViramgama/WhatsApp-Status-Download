package com.app.status.util

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.app.status.BuildConfig
import com.app.status.R
import com.app.status.ui.home.item.DataItem

object Const {

    val whatsApp: String =
        "${Environment.getExternalStorageDirectory()}${BuildConfig.whatsApp}"

    const val whatsApp11: String = BuildConfig.whatsApp11

    val whatsAppBusiness: String =
        "${Environment.getExternalStorageDirectory()}${BuildConfig.whatsAppBusiness}"

    const val whatsAppBusiness11: String =
        BuildConfig.whatsAppBusiness11

    const val whatsAppPackageName: String = "com.whatsapp"
    const val whatsAppBussinessPackageName: String = "com.whatsapp.w4b"

    var dataItem: MutableList<DataItem> = arrayListOf()
    var isDark by mutableStateOf(false)

    val options = listOf("Device default", "Dark", "Light")

}

fun Context.getDownloadPath(): String {
    return "${cacheDir.absolutePath}/.${
        resources.getString(
            R.string.app_name
        )
    }"
}