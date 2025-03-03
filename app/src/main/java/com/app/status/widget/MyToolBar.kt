package com.app.status.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.status.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyToolBar(title: String = "", onBackClick: () -> Unit) {
    TopAppBar(
        title = { MyText(text = title) },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick.invoke()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.app_name)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}