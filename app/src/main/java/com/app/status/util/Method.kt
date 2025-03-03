package com.app.status.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.app.status.util.Const.options
import com.app.status.widget.MyText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemDialog(selectedOption: Int, onDismiss: () -> Unit, onResult: (type: Int) -> Unit) {

    BasicAlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                options.forEachIndexed { index, option ->
                    Row(modifier = Modifier.clickable {
                        onDismiss.invoke()
                    }) {
                        RadioButton(
                            selected = selectedOption == index,
                            onClick = {
                                onResult.invoke(index)
                            },
                        )
                        MyText(
                            text = option, modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}