package com.app.status.ui.home.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.app.status.R
import com.app.status.ui.home.item.DataItem
import com.app.status.ui.showImage.ShowImage
import com.app.status.util.Const.dataItem
import com.app.status.widget.LoaderDialog
import com.app.status.util.ResponseData
import com.app.status.util.showInterstitial
import com.app.status.widget.NoDataFound
import com.app.status.widget.ProgressBar


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Video(activity: Activity, videoViewModel: VideoViewModel) {

    val fileList: MutableList<DataItem> = remember {
        mutableStateListOf()
    }

    var isShowData by remember { mutableStateOf(false) }
    var isShowProgressBar by remember { mutableStateOf(true) }
    var isShowLoader by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        videoViewModel.getVideo()
        videoViewModel.videoState.collect() {
            when (it) {

                is ResponseData.Success -> {
                    isShowProgressBar = false
                    if (!it.data.isNullOrEmpty()) {
                        fileList.clear()
                        fileList.addAll(it.data)
                        isShowData = true
                    }
                }

                is ResponseData.Loading -> {
                    isShowProgressBar = true
                }

                is ResponseData.Error -> {
                    isShowProgressBar = false
                    Toast.makeText(activity, it.error, Toast.LENGTH_SHORT).show()
                }

                is ResponseData.InternetConnection -> {
                    isShowProgressBar = false
                    Toast.makeText(activity, it.error, Toast.LENGTH_SHORT).show()
                }

                is ResponseData.Empty -> {}
            }
        }

    }

    if (isShowData) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(fileList) { index, item ->
                GlideImage(model = item.filePath,
                    contentDescription = activity.resources.getString(R.string.app_name),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.ic_launcher_background
                        )
                    ),
                    modifier = Modifier
                        .height(180.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .clickable {
                            isShowLoader = true
                            activity.showInterstitial {
                                isShowLoader = false
                                dataItem.clear()
                                dataItem.addAll(fileList)
                                activity.startActivity(
                                    Intent(
                                        activity,
                                        ShowImage::class.java
                                    )
                                        .putExtra("position", index)
                                        .putExtra("isDownload", false)
                                )
                            }
                        }) {
                    it.placeholder(R.mipmap.ic_launcher)
                }
            }
        }
    } else if (isShowProgressBar) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ProgressBar()
        }
    } else {
        NoDataFound()
    }

    if (isShowLoader) {
        LoaderDialog(true)
    }


}