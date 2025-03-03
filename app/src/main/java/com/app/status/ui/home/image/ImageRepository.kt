package com.app.status.ui.home.image

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.app.status.ui.home.item.DataItem
import com.app.status.util.Const.whatsAppBussinessPackageName
import com.app.status.util.Const.whatsAppPackageName
import com.app.status.util.isAppInstalled
import com.app.status.util.path
import com.app.status.util.pathBusiness
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class ImageRepository @Inject constructor(val context: Context) {

    fun getData(): Flow<MutableList<DataItem>> {
        return flow {
            val dataItem: MutableList<DataItem> = arrayListOf()
            if (context.isAppInstalled(whatsAppPackageName)) {
                dataItem.addAll(findData(path()))
            }
            if (context.isAppInstalled(whatsAppBussinessPackageName)) {
                dataItem.addAll(findData(pathBusiness()))
            }
            emit(dataItem)
        }.flowOn(Dispatchers.IO)
    }

    private fun findData(path: String): MutableList<DataItem> {
        val dataItem: MutableList<DataItem> = arrayListOf()
        if (Build.VERSION.SDK_INT > 29) {
            val documentFile = DocumentFile.fromTreeUri(
                context,
                Uri.parse(path)
            )
            if (documentFile != null && documentFile.isDirectory) {
                documentFile.listFiles().filter {
                    it.name.toString().contains(".jpg") || it.name.toString()
                        .contains(".jpeg") || it.name.toString()
                        .contains(".png") || it.name.toString().contains(".wepb")
                }.mapIndexed { _, item ->
                    dataItem.add(DataItem(item.name.toString(), item.uri.toString()))
                }
            }
        } else {
            File(
                path
            ).listFiles { file ->
                file.path.contains(".jpg") || file.path.contains(".jpeg") || file.path.contains(
                    ".png"
                ) || file.path.contains(
                    ".wepb"
                )
            }?.mapIndexed { _, item ->
                dataItem.add(DataItem(item.name, item.path))
            }
        }
        return dataItem
    }

}