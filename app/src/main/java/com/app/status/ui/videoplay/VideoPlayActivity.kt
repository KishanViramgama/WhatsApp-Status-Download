package com.app.status.ui.videoplay

import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.app.status.base.BaseActivity
import com.app.status.widget.MyAdView

class VideoPlayActivity : BaseActivity() {

    private lateinit var url: String
    private lateinit var mExoPlayer: ExoPlayer

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                releasePlayer()
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        releasePlayer()
                        finish()
                    }
                })
        }

        url = intent.getStringExtra("url").toString()


        setContent {

            // Declaring ExoPlayer
            mExoPlayer = remember(this) {
                ExoPlayer.Builder(this).build().apply {

                    /*val userAgent = Util.getUserAgent(this@VideoPlayActivity, packageName)
                    val dataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)*/

                    val dataSourceFactory = DefaultDataSourceFactory(
                        this@VideoPlayActivity,
                        Util.getUserAgent(this@VideoPlayActivity, packageName)
                    )
                    val source =
                        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                            MediaItem.fromUri(url)
                        )
                    setMediaSource(source)
                    prepare()
                }
            }


            Surface {
                Scaffold(modifier = Modifier.navigationBarsPadding(),
                    content = {
                        // Implementing ExoPlayer
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    player = mExoPlayer
                                }
                            }, modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                                .background(Color.Black)
                        )
                    },
                    bottomBar = {
                        MyAdView()
                    }
                )

            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (mExoPlayer.isPlaying) {
            mExoPlayer.playWhenReady = false
        }
    }

    fun releasePlayer() {
        if (mExoPlayer.isPlaying) {
            mExoPlayer.playWhenReady = false
            mExoPlayer.stop()
            mExoPlayer.release()
        }
    }

}