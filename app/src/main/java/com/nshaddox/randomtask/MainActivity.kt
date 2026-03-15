package com.nshaddox.randomtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.nshaddox.randomtask.ui.navigation.NavGraph
import com.nshaddox.randomtask.ui.screens.settings.SettingsViewModel
import com.nshaddox.randomtask.ui.theme.RandomTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val uiState by settingsViewModel.uiState.collectAsState()
            val darkTheme = resolveTheme(uiState.appTheme, isSystemInDarkTheme())

            RandomTaskTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
