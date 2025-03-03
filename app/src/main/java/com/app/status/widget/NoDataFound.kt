package com.app.status.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.status.R
import com.app.status.util.FontEnum


@Composable
@Preview
fun NoDataFound() {
    MyText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        text = stringResource(R.string.no_data_found),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        fontFamily = FontEnum.BOlD
    )
}