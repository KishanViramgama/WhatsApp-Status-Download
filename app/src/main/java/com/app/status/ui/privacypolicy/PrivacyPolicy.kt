package com.app.status.ui.privacypolicy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.app.status.R
import com.app.status.base.BaseActivity
import com.app.status.ui.theme.StatusThem
import com.app.status.util.Const.isDark
import com.app.status.widget.MyAdView
import com.app.status.widget.MyToolBar
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class PrivacyPolicy : BaseActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val str: String = try {
            val data = assets.open("privarcypolicy.txt")
            val size = data.available()
            val buffer = ByteArray(size) // Read the entire asset into a local byte buffer.
            data.read(buffer)
            data.close()
            String(buffer) // Convert the buffer into a string.
        } catch (e: IOException) {
            throw RuntimeException(e) // Should never happen!
        }

        setContent {
            StatusThem(isDark) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold( modifier = Modifier.navigationBarsPadding(),topBar = {
                        MyToolBar(title = resources.getString(R.string.privacy_policy)) {
                            finish()
                        }
                    }, bottomBar = {
                        MyAdView()
                    }) {
                        Column(
                            modifier = Modifier
                                .padding(it)
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            AndroidView(factory = { itContext ->
                                WebView(itContext).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    settings.defaultTextEncodingName = "UTF-8"
                                    loadDataWithBaseURL(null, str, "text/html", "utf-8", null)
                                }
                            })
                        }
                    }
                }
            }

        }
    }

}