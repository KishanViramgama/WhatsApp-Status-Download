package com.app.status.util

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.app.status.R

enum class FontEnum(val fontFamily: FontFamily) {

    BOlD(FontFamily(Font(R.font.poppins_semi_bold))),
    REGULAR(FontFamily(Font(R.font.poppins_regular)))

}