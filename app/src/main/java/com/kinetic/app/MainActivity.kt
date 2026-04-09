package com.kinetic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kinetic.app.ui.navigation.NavGraph
import com.kinetic.app.ui.theme.KineticTheme
import com.kinetic.app.ui.theme.Lime
import com.kinetic.app.ui.viewmodels.SettingsViewModel
import com.kinetic.app.ui.viewmodels.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val startupViewModel: StartupViewModel = hiltViewModel()
            val startupState by startupViewModel.uiState.collectAsStateWithLifecycle()

            KineticTheme(darkTheme = settingsState.darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (startupState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Lime)
                        }
                    } else {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            startDestination = startupState.startDestination
                        )
                    }
                }
            }
        }
    }
}