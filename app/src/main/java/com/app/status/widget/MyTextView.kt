package com.app.status.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.app.status.util.FontEnum

@Composable
fun MyText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign =TextAlign.Unspecified,
    fontFamily: FontEnum = FontEnum.REGULAR,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow,
        fontSize = fontSize,
        textAlign = textAlign,
        fontFamily = fontFamily.fontFamily,
        color = color
    )
}
