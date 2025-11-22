package com.subin.leafy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.screen.EntryPointScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafyTheme {

                EntryPointScreen()
            }
        }
    }
}

