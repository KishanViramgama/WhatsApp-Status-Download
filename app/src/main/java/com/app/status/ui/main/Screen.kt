package com.app.status.ui.main

sealed class Screen(val route: String){
    data object Home: Screen("Home")
    data object Setting: Screen("Setting")
}
