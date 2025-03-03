package com.app.status.ui.home.download

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.status.ui.home.item.DataItem
import com.app.status.util.ResponseData
import com.app.status.util.failMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val context: Context,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    /**
     * Set default empty
     */

    private val downloadMutableState: MutableStateFlow<ResponseData<MutableList<DataItem>>> =
        MutableStateFlow(ResponseData.Empty())
    val downloadState: StateFlow<ResponseData<MutableList<DataItem>>> = downloadMutableState

    fun getDownload() {

        viewModelScope.launch {
            downloadRepository.getData().onStart {
                downloadMutableState.value = ResponseData.Loading()
            }.catch {
                downloadMutableState.value =
                    ResponseData.Error(null, context.failMsg(error = it.toString()))
            }.collect {
                downloadMutableState.value = ResponseData.Success(it)
            }
        }

    }

}