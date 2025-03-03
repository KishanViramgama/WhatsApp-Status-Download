package com.app.status.ui.home.download

import android.content.Context
import com.app.status.ui.home.item.DataItem
import com.app.status.util.getDownloadPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class DownloadRepository @Inject constructor(val context: Context) {

    suspend fun getData(): Flow<MutableList<DataItem>> {
        return flow {
            val dataItem: MutableList<DataItem> = arrayListOf()
            File(
                context.getDownloadPath(),
            ).listFiles()?.mapIndexed { _, item ->
                dataItem.add(DataItem(item.name, item.path))
            }
            emit(dataItem)
        }.flowOn(Dispatchers.IO)
    }

}