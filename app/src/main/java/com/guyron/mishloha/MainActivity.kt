package com.guyron.mishloha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.guyron.mishloha.presentation.ui.trending.TrendingRepositoriesScreen
import com.guyron.mishloha.presentation.utils.theme.MishlohaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MishlohaTheme {
                    TrendingRepositoriesScreen({},{})

            }
        }
    }
}

