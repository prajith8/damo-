package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ResumeViewModel
import com.example.ui.Screen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.PreviewScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ResumeViewModel by viewModels {
        ResumeViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                    Screen.Editor -> EditorScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(Screen.Dashboard) },
                        modifier = Modifier.fillMaxSize()
                    )
                    Screen.Preview -> PreviewScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(Screen.Dashboard) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
