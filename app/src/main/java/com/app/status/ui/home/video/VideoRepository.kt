package com.app.status.ui.home.video

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.app.status.ui.home.item.DataItem
import com.app.status.util.Const
import com.app.status.util.isAppInstalled
import com.app.status.util.path
import com.app.status.util.pathBusiness
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class VideoRepository @Inject constructor(val context: Context) {

    fun getData(): Flow<MutableList<DataItem>> {
        return flow {
            val dataItem: MutableList<DataItem> = arrayListOf()
            if (context.isAppInstalled(Const.whatsAppPackageName)) {
                dataItem.addAll(findData(path()))
            }
            if (context.isAppInstalled(Const.whatsAppBussinessPackageName)) {
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
                    it.name.toString().contains(".mp4") || it.name.toString().contains(".gif")
                }.mapIndexed { _, item ->
                    dataItem.add(DataItem(item.name.toString(), item.uri.toString()))
                }
            }
        } else {
            File(
                path
            ).listFiles { file ->
                file.path.contains(".mp4") || file.path.contains(".gif")
            }?.mapIndexed { _, item ->
                dataItem.add(DataItem(item.name, item.path))
            }
        }
        return dataItem
    }

}