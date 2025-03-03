package com.app.status.ui.setting

import androidx.lifecycle.ViewModel
import com.app.status.datastore.MyDataStore
import com.app.status.datastore.MyDataStore.Companion.isDelete
import com.app.status.datastore.MyDataStore.Companion.themSettingKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
) : ViewModel() {

    @Inject
    lateinit var myDataStore: MyDataStore

    fun themType(): Int = runBlocking {
        return@runBlocking myDataStore.getMyDataStoreString(themSettingKey, "0").first()
            .toInt()
    }

    fun isDeleteSwitch(): Boolean = runBlocking {
        return@runBlocking myDataStore.getMyDataStoreBoolean(isDelete, false).first()
    }

}