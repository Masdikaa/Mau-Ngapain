package com.masdika.maungapain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.masdika.maungapain.ui.screen.TaskScreen
import com.masdika.maungapain.ui.theme.MauNgapainTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MauNgapainTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    TaskScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}