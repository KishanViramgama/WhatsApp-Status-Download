package com.app.status.ui.main.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.app.status.R
import com.app.status.widget.MyText

/**
 * Drawer Navigation Header
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
@Preview(showBackground = true)
fun NavHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            model = R.mipmap.ic_launcher,
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .padding(end = 10.dp)
        ) {
            it.placeholder(R.mipmap.ic_launcher).circleCrop()
        }
        MyText(
            text = stringResource(id = R.string.app_name),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}