package com.app.status.util

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.app.status.BuildConfig
import com.app.status.R
import com.app.status.util.Const.options
import com.app.status.util.Const.whatsApp
import com.app.status.util.Const.whatsApp11
import com.app.status.util.Const.whatsAppBusiness
import com.app.status.util.Const.whatsAppBusiness11
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


fun Context.failMsg(error: String, msg: String = resources.getString(R.string.wrong)): String {
    return if (BuildConfig.DEBUG) {
        error
    } else {
        msg
    }
}

fun Context.myToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
infix fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun ShowMyDialog(
    positive: (() -> Unit)? = null,
    negative: (() -> Unit)? = null,
    title: String = "",
    msg: String = "",
    positiveText: String = "",
    negativeText: String = "",
    isShowConfirm: Boolean = true,
    isShowDismiss: Boolean = true
) {
    AlertDialog(onDismissRequest = {}, title = {
        Text(title)
    }, text = {
        Text(msg)
    }, confirmButton = {
        if (isShowConfirm) {
            TextButton(
                onClick = positive!!
            ) {
                Text(positiveText)
            }
        }
    }, dismissButton = {
        if (isShowDismiss) {
            TextButton(
                onClick = negative!!
            ) {
                Text(negativeText)
            }
        }
    })
}

fun Context.isVideo(string: String): Boolean {
    return string.contains(".mp4")
}

fun Context.isPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkMultiplePermissions(
    vararg permissions: String,
): Boolean {
    val deniedPermissions = permissions.filter { permission ->
        ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED
    }
    return deniedPermissions.isEmpty()
}

//Check dark mode or not
fun Context.isDarkMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }
}

/**
 * Get theme value
 */

fun Context.themValue(themType: String): Boolean {
    return when (themType) {
        "0" -> {
            isDarkMode()
        }

        "1" -> {
            true
        }

        "2" -> {
            false
        }

        else -> {
            false
        }
    }
}

fun Context.themName(index: Int): String {
    return options[index]
}


fun path(): String {
    return if (Build.VERSION.SDK_INT > 29) {
        convertPathToContentUri(whatsApp11).toString()
    } else {
        whatsApp
    }
}

fun pathBusiness(): String {
    return if (Build.VERSION.SDK_INT > 29) {
        convertPathToContentUri(whatsAppBusiness11).toString()
    } else {
        whatsAppBusiness
    }
}

fun convertPathToContentUri(folderPath: String): Uri {
    val baseUri = "content://com.android.externalstorage.documents/tree/"
    val encodedPath = Uri.encode("primary:$folderPath")
    return Uri.parse(baseUri + encodedPath)
}

/**
 * Get file path from uri
 * @param uri - file uri
 */
fun Context.getPath(uri: String): String? {
    return if (uri.contains("content://")) {
        PathUtil.getPath(context = this, uri = Uri.parse(uri))
    } else {
        uri
    }
}

/**
 * Share file
 * @param link - file path
 */
fun Context.share(link: String, isFromDownload: Boolean = false) {

    val contentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        if (isFromDownload) {
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".fileprovider", File(link)
            )
        } else {
            FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                File(shareFile(this, Uri.parse(link).toString()))
            )
        }
    } else {
        FileProvider.getUriForFile(
            this, BuildConfig.APPLICATION_ID + ".fileprovider", File(link)
        )
    }

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    if (isVideo(link)) {
        shareIntent.type = "video/*"
    } else {
        shareIntent.type = "image/*"
    }
    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
    shareIntent.clipData = ClipData.newRawUri("", contentUri)

    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    shareIntent.putExtra(
        Intent.EXTRA_TEXT, resources.getString(R.string.playMoreApp)
    )
    startActivity(
        Intent.createChooser(
            shareIntent, resources.getString(R.string.share_to)
        )
    )

}

/**
 * Check application install or not
 * @param packageName - application package name
 */
fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.showInterstitial(onDismiss: () -> Unit) {
    onDismiss()
}

fun String.encodedUrl(charset: String = "UTF-8") = try {
    URLEncoder.encode(this, charset)
} catch (e: UnsupportedEncodingException) {
    null
}
