package com.app.status.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun LoaderDialog(isDisplayed: Boolean) {
    if (isDisplayed) {
        Dialog(onDismissRequest = { /* Handle dismiss logic */ }) {
            Box(
                contentAlignment = Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(shape = RoundedCornerShape(10.dp), color = Color.White)
            ) {
                CircularProgressIndicator() // Or any other loader indicator
            }
        }
    }
}