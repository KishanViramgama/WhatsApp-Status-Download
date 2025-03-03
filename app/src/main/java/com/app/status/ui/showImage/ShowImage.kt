package com.app.status.ui.showImage

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.GsonBuilder
import com.app.status.R
import com.app.status.base.BaseActivity
import com.app.status.datastore.MyDataStore.Companion.isDelete
import com.app.status.ui.home.item.DataItem
import com.app.status.ui.theme.StatusThem
import com.app.status.ui.theme.detailOptionBG
import com.app.status.ui.videoplay.VideoPlayActivity
import com.app.status.util.Const
import com.app.status.util.Const.isDark
import com.app.status.util.ShowMyDialog
import com.app.status.util.failMsg
import com.app.status.util.getDownloadPath
import com.app.status.util.isVideo
import com.app.status.util.myToast
import com.app.status.util.noRippleClickable
import com.app.status.util.share
import com.app.status.widget.MyAdView
import com.app.status.widget.SocialOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.app.status.util.shareFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.abs

@AndroidEntryPoint
class ShowImage : BaseActivity() {

    private var position by mutableIntStateOf(0)
    private var isDownload: Boolean = false
    private var isVideo by mutableStateOf(false)

    private lateinit var dataItem: MutableList<DataItem>
    private var removePositions: MutableList<Int> = arrayListOf()

    /**
     * Show Delete Option
     */
    private var isShowDeleteOption: Boolean = false
    private var isShowDeleteDialog by mutableStateOf(false)

    @OptIn(
        ExperimentalGlideComposeApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(this, R.color.transparent),
                ContextCompat.getColor(this, R.color.transparent)
            )
        )

        dataItem = arrayListOf()
        dataItem.addAll(Const.dataItem)

        position = intent.getIntExtra("position", 0)
        isDownload = intent.getBooleanExtra("isDownload", false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finishActivity()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishActivity()
                }
            })
        }

        CoroutineScope(Dispatchers.IO).launch {
            isShowDeleteOption = myDataStore.getMyDataStoreBoolean(isDelete, false).first()
        }

        setContent {

            val (items, setItems) = remember { mutableStateOf(dataItem) }
            val state = rememberPagerState(initialPage = position) { items.size }

            StatusThem(isDark) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.Black)
                        ) {
                            HorizontalPager(
                                state = state,
                                modifier = Modifier.background(Color.Transparent),
                            ) { page ->
                                if (items.size > page) {
                                    //isVideo = isVideo(items[page].filePath)
                                    Box(modifier = Modifier.graphicsLayer {
                                        val pageOffset =
                                            (state.currentPage - page) + state.currentPageOffsetFraction
                                        alpha = 1f - abs(pageOffset)
                                        scaleX = 1f - abs(pageOffset) * 0.2f
                                        scaleY = 1f - abs(pageOffset) * 0.2f
                                    }) {
                                        GlideImage(
                                            model = items[page].filePath,
                                            contentDescription = getString(R.string.app_name),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                        ) {
                                            it.placeholder(R.mipmap.ic_launcher)
                                        }
                                        if (isVideo(items[page].filePath)) {
                                            Image(painter = painterResource(R.drawable.play),
                                                contentDescription = stringResource(R.string.app_name),
                                                modifier = Modifier
                                                    .align(
                                                        Alignment.Center
                                                    )
                                                    .noRippleClickable {
                                                        startActivity(
                                                            Intent(
                                                                this@ShowImage,
                                                                VideoPlayActivity::class.java
                                                            ).putExtra(
                                                                "url", items[page].filePath
                                                            )
                                                        )
                                                    }
                                                    .height(48.dp)
                                                    .width(48.dp))
                                        }
                                    }
                                }

                            }
                            IconButton(onClick = {
                                onBackPressedDispatcher.onBackPressed()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    tint = Color.White,
                                    contentDescription = stringResource(R.string.app_name),
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(detailOptionBG)
                            ) {
                                SocialOption(icon = R.drawable.share,
                                    text = stringResource(R.string.share),
                                    modifier = Modifier
                                        .weight(1f)
                                        .noRippleClickable {
                                            share(
                                                items[state.currentPage].filePath,
                                                isDownload
                                            )
                                        })
                                /*if (!isVideo) {
                                    SocialOption(icon = R.drawable.ic_wallpaper,
                                        text = stringResource(R.string.wallpaper),
                                        modifier = Modifier
                                            .weight(1f)
                                            .noRippleClickable {
                                                setHomeScreenWallpaper(
                                                    this@ShowImage, items[state.currentPage].filePath
                                                )
                                            })
                                }*/
                                if (!isDownload) {
                                    SocialOption(icon = R.drawable.download,
                                        text = stringResource(R.string.download),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                val destinationPath = getDownloadPath()
                                                val directory = File(destinationPath)
                                                if (!directory.exists()) {
                                                    directory.mkdirs()
                                                }
                                                copyFile(
                                                    items[state.currentPage].filePath,
                                                    "$destinationPath/${items[state.currentPage].fileName}"
                                                )
                                            })
                                }
                                if (isDownload) {
                                    SocialOption(icon = R.drawable.delete,
                                        text = stringResource(R.string.delete),
                                        modifier = Modifier
                                            .weight(1f)
                                            .noRippleClickable {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    if (isShowDeleteOption) {
                                                        isShowDeleteDialog = true
                                                    } else {
                                                        deleteFile(items, state.currentPage) {
                                                            setItems(it)
                                                        }
                                                    }
                                                }
                                            })
                                }
                                if (isShowDeleteDialog) {
                                    ShowMyDialog(
                                        title = stringResource(R.string.app_name),
                                        msg = stringResource(R.string.delete_conformation_msg),
                                        positiveText = stringResource(id = R.string.yes),
                                        negativeText = stringResource(id = R.string.no),
                                        positive = {
                                            deleteFile(items, state.currentPage) {
                                                setItems(it)
                                            }
                                        },
                                        negative = { isShowDeleteDialog = false },
                                    )
                                }
                            }
                        }
                        MyAdView()
                    }
                }
            }
        }

    }

    private fun finishActivity() {
        val data = Intent()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val string: String = gson.toJson(removePositions)
        data.putExtra("data", string)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun deleteFile(
        list: MutableList<DataItem>,
        getPosition: Int,
        updateItems: (MutableList<DataItem>) -> Unit
    ) {
        File(list[getPosition].filePath).delete()
        val updatedItems = list.toMutableList()
        updatedItems.removeAt(getPosition)
        removePositions.add(getPosition)
        if (updatedItems.size == 0) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            updateItems(updatedItems)
        }
    }

    private fun setHomeScreenWallpaper(context: Context, imagePath: String) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val bitmap = BitmapFactory.decodeFile(imagePath)
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun copyFile(sourcePath: String, destinationPath: String) {
        try {

            val tempSourcePath = if (sourcePath.contains("content://")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    shareFile(this, Uri.parse(sourcePath).toString())
                } else {
                    getPathFromUri(Uri.parse(sourcePath))
                }
            } else {
                sourcePath
            }

            val fileInputStream = FileInputStream(tempSourcePath)
            val out = FileOutputStream(destinationPath)
            val buffer = ByteArray(1024)
            var read: Int
            while ((fileInputStream.read(buffer).also { read = it }) != -1) {
                out.write(buffer, 0, read)
            }
            fileInputStream.close()
            out.flush()
            out.close()
            myToast(resources.getString(R.string.download))
        } catch (e: Exception) {
            e.printStackTrace()
            myToast(resources.getString(R.string.wrong))
            Log.d("data_information", "Fail message ${failMsg(e.toString())}")
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var filePath: String? = null
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
            }
        }
        return filePath
    }

}