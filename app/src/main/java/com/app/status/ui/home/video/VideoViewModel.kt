package com.app.status.ui.home.video

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
class VideoViewModel @Inject constructor(
    private val context: Context,
    private val videoRepository: VideoRepository
) : ViewModel() {

    /**
     * Set default empty
     */

    val videoMutableState: MutableStateFlow<ResponseData<MutableList<DataItem>>> =
        MutableStateFlow(ResponseData.Empty())
    val videoState : StateFlow<ResponseData<MutableList<DataItem>>> = videoMutableState


    fun getVideo() {
        viewModelScope.launch {
            videoRepository.getData().onStart {
                videoMutableState.value = ResponseData.Loading()
            }.catch {
                videoMutableState.value =
                    ResponseData.Error(null, context.failMsg(error = it.toString()))
            }.collect {
                videoMutableState.value = ResponseData.Success(it)
            }
        }
    }

}