package com.app.status.ui.setting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.status.R
import com.app.status.datastore.MyDataStore.Companion.isDelete
import com.app.status.datastore.MyDataStore.Companion.themSettingKey
import com.app.status.ui.aboutUs.AboutUs
import com.app.status.ui.privacypolicy.PrivacyPolicy
import com.app.status.util.ThemDialog
import com.app.status.util.themName
import com.app.status.util.themValue
import com.app.status.widget.ImageText
import com.app.status.widget.MyText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Setting(
    activity: Activity,
    viewModel: SettingViewModel = hiltViewModel(),
    onClick: (isThem: Boolean) -> Unit
) {

    var themOption by remember { mutableIntStateOf(0) }
    var switchCheckedState by remember { mutableStateOf(false) }
    var isShowThemDialog by remember { mutableStateOf(false) }

    LaunchedEffect(themOption) {
        themOption = viewModel.themType()
    }
    switchCheckedState = viewModel.isDeleteSwitch()

    Column(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clickable {
                isShowThemDialog = true
            }) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                val (titleText, subTitleText, image) = createRefs()
                createVerticalChain(titleText, subTitleText, chainStyle = ChainStyle.Packed)
                Icon(painter = painterResource(R.drawable.ic_them),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .constrainAs(image) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(40.dp)
                        .height(40.dp)
                        .padding(end = 10.dp))
                MyText(text = stringResource(id = R.string.them),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(titleText) {
                        start.linkTo(image.end)
                        top.linkTo(image.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
                MyText(text = activity.themName(themOption),
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                val (textView, switch) = createRefs()
                MyText(
                    text = stringResource(id = R.string.delete_conformation),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .constrainAs(textView) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(switch.start)
                            width = Dimension.fillToConstraints
                        },
                )
                Switch(modifier = Modifier.constrainAs(switch) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }, checked = switchCheckedState, onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.myDataStore.setMyDataStoreBoolean(
                            isDelete, it
                        )
                    }
                    switchCheckedState = it
                })
            }
        }
        ImageText(R.drawable.ic_about_us, stringResource(R.string.aboutUs), activity) {
            activity.startActivity(Intent(activity, AboutUs::class.java))
        }
        ImageText(R.drawable.ic_about_us, stringResource(R.string.privacy_policy), activity) {
            activity.startActivity(Intent(activity, PrivacyPolicy::class.java))
        }
        ImageText(R.drawable.ic_share, stringResource(R.string.share_app), activity) {
            shareApp(activity)
        }
        ImageText(R.drawable.ic_rate, stringResource(R.string.rate_app), activity) {
            rateApp(activity)
        }
        ImageText(R.drawable.ic_more, stringResource(R.string.more_app), activity) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        activity.resources.getString(R.string.playMoreApp)
                    )
                )
            )
        }
    }

    if (isShowThemDialog) {
        ThemDialog(themOption, onDismiss = {
            isShowThemDialog = false
        }, onResult = { type ->
            isShowThemDialog = false
            CoroutineScope(Dispatchers.IO).launch {
                themOption = type
                viewModel.myDataStore.setMyDataStoreString(themSettingKey, type.toString())
                onClick.invoke(activity.themValue(type.toString()))
            }
        })

    }

}


fun rateApp(activity: Activity) {
    val uri = Uri.parse("market://details?id=" + activity.application.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        activity.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + activity.application.packageName)
            )
        )
    }
}

fun shareApp(activity: Activity) {
    try {
        val string =
            "${activity.resources.getString(R.string.Let_me_recommend_you_this_application)} https://play.google.com/store/apps/details?id=${activity.application.packageName}"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, string)
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.resources.getString(R.string.app_name))
        activity.startActivity(
            Intent.createChooser(
                intent, activity.resources.getString(R.string.choose_one)
            )
        )
    } catch (e: Exception) {
        e.toString()
    }
}