package com.app.status.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File

fun shareFile(context: Context, sourceFile: String): String {
    val desPath = "${context.filesDir}/Image-s.jpg"
    val destUri = Uri.fromFile(File(desPath))
    Log.d("moveFile", destUri.toString())
    try {
        val `is` = context.contentResolver.openInputStream(Uri.parse(sourceFile))
        val os = context.contentResolver.openOutputStream(destUri, "w")
        val buffer = ByteArray(1024)
        while (true) {
            val read = `is`!!.read(buffer)
            if (read > 0) {
                os!!.write(buffer, 0, read)
            } else {
                `is`.close()
                os!!.flush()
                os.close()
                val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                intent.setData(destUri)
                context.sendBroadcast(intent)
                return desPath
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.d("moveFile", e.toString())
        return ""
    }
}
