package com.app.status.widget

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.app.status.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageText(icon: Int, title: String, activity: Activity, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
        .clickable {
            onClick.invoke()
        }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val (image, titleText) = createRefs()
            Icon(painter = painterResource(icon),
                contentDescription = activity.resources.getString(R.string.app_name),
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
                    bottom.linkTo(image.bottom)
                    width = Dimension.fillToConstraints
                })
        }
    }
}

@Composable
fun SocialOption(icon: Int, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            tint = Color.White,
            contentDescription = "Drawer Icon",
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )
        MyText(text = text, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
    }
}