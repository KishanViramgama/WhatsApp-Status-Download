package com.app.status.ui.home.image

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
class ImageViewModel @Inject constructor(
    private val context: Context,
    private val imageRepository: ImageRepository
) : ViewModel() {

    /**
     * Set default empty
     */

    private val imageMutableState: MutableStateFlow<ResponseData<MutableList<DataItem>>> =
        MutableStateFlow(ResponseData.Empty())
    val imageState: StateFlow<ResponseData<MutableList<DataItem>>> = imageMutableState

    fun getImage() {

        viewModelScope.launch {
            imageRepository.getData().onStart {
                imageMutableState.value = ResponseData.Loading()
            }.catch {
                imageMutableState.value =
                    ResponseData.Error(null, context.failMsg(error = it.toString()))
            }.collect {
                imageMutableState.value = ResponseData.Success(it)
            }
        }

    }

}