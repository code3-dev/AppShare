package com.pira.appshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.pira.appshare.data.PreferencesManager
import com.pira.appshare.ui.AppListScreen
import com.pira.appshare.ui.SettingsScreen
import com.pira.appshare.ui.theme.AppShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation(this)
        }
    }
}

@Composable
fun AppNavigation(activity: ComponentActivity) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager.getInstance(context)
    var currentScreen by rememberSaveable { mutableStateOf("app_list") }
    var themePreference by rememberSaveable { mutableStateOf(preferencesManager.themePreference) }
    
    AppShareTheme(themePreference = themePreference) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "app_list" -> AppListScreen(
                    onSettingsClick = { currentScreen = "settings" }
                )
                "settings" -> SettingsScreen(
                    onBackClick = { currentScreen = "app_list" },
                    currentTheme = themePreference,
                    onThemeChange = { 
                        themePreference = it
                        preferencesManager.themePreference = it
                    },
                    onExitApp = { activity.finish() },
                    showSystemApps = preferencesManager.showSystemApps,
                    onShowSystemAppsChange = { 
                        preferencesManager.showSystemApps = it
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppListPreview() {
    AppShareTheme {
        AppListScreen(
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferencesManager = PreferencesManager.getInstance(context)
    
    AppShareTheme {
        SettingsScreen(
            onBackClick = {},
            onExitApp = {},
            showSystemApps = preferencesManager.showSystemApps,
            onShowSystemAppsChange = {}
        )
    }
}