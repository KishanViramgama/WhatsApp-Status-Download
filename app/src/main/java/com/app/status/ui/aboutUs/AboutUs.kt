package com.app.status.ui.aboutUs

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.app.status.BuildConfig
import com.app.status.R
import com.app.status.base.BaseActivity
import com.app.status.ui.theme.StatusThem
import com.app.status.util.Const.isDark
import com.app.status.widget.MyAdView
import com.app.status.widget.MyText
import com.app.status.widget.MyToolBar

class AboutUs : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StatusThem(isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.navigationBarsPadding(), topBar = {
                        MyToolBar(title = resources.getString(R.string.aboutUs)) {
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
                            AppName()
                            OtherView(
                                R.drawable.ic_about_us,
                                stringResource(R.string.version),
                                BuildConfig.VERSION_NAME
                            )
                            OtherView(
                                R.drawable.ic_email,
                                stringResource(R.string.email),
                                stringResource(R.string.emailId)
                            )
                            AboutUS()
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun OtherView(icon: Int, title: String, subTitle: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                val (titleText, subTitleText, image) = createRefs()
                createVerticalChain(titleText, subTitleText, chainStyle = ChainStyle.Packed)
                Icon(painter = painterResource(icon),
                    contentDescription = resources.getString(R.string.aboutUs),
                    modifier = Modifier
                        .constrainAs(image) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(40.dp)
                        .height(40.dp)
                        .padding(end = 10.dp))
                MyText(text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(titleText) {
                        start.linkTo(image.end)
                        top.linkTo(image.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
                MyText(text = subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(subTitleText) {
                        start.linkTo(titleText.start)
                        end.linkTo(parent.end)
                        top.linkTo(titleText.bottom)
                        bottom.linkTo(image.bottom)
                        width = Dimension.fillToConstraints
                    })
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun AppName() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                GlideImage(
                    model = R.mipmap.ic_launcher_round,
                    contentDescription = getString(R.string.app_name),
                    modifier = Modifier
                        .height(45.dp)
                        .width(45.dp)
                ) { itGlide ->
                    itGlide.placeholder(R.mipmap.ic_launcher_round)
                }
                MyText(
                    text = resources.getString(R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun AboutUS() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                MyText(
                    text = stringResource(R.string.aboutUs),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                MyText(
                    text = stringResource(R.string.aboutUsDetail),
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }

}