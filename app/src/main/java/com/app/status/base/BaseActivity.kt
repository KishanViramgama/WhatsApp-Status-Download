package com.app.status.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.app.status.datastore.MyDataStore
import javax.inject.Inject

open class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var myDataStore: MyDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

}