package com.pira.appshare.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pira.appshare.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    currentTheme: String = "system",
    onThemeChange: (String) -> Unit = {},
    onExitApp: () -> Unit = {},
    showSystemApps: Boolean = false,
    onShowSystemAppsChange: (Boolean) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showSystemAppsState by remember { mutableStateOf(showSystemApps) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var cacheSize by remember { mutableStateOf(ShareUtils.getCacheSize(context)) }
    val scrollState = rememberScrollState()

    // Update cache size when screen is composed
    cacheSize = ShareUtils.getCacheSize(context)

    // Handle back button press
    BackHandler {
        onBackClick()
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit App") },
            text = { Text("Are you sure you want to exit the application?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExitApp()
                    }
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cache") },
            text = { Text("Are you sure you want to clear the app cache? This will remove all temporarily stored APK files.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ShareUtils.clearCache(context)
                        cacheSize = 0
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCacheDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Theme Selection
            Text(
                text = "Theme",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            // Light Theme Option
            ListItem(
                headlineContent = { Text("Light") },
                leadingContent = {
                    RadioButton(
                        selected = selectedTheme == "light",
                        onClick = {
                            selectedTheme = "light"
                            onThemeChange("light")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTheme = "light"
                        onThemeChange("light")
                    }
            )
            
            // Dark Theme Option
            ListItem(
                headlineContent = { Text("Dark") },
                leadingContent = {
                    RadioButton(
                        selected = selectedTheme == "dark",
                        onClick = {
                            selectedTheme = "dark"
                            onThemeChange("dark")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTheme = "dark"
                        onThemeChange("dark")
                    }
            )
            
            // System Theme Option
            ListItem(
                headlineContent = { Text("System") },
                leadingContent = {
                    RadioButton(
                        selected = selectedTheme == "system",
                        onClick = {
                            selectedTheme = "system"
                            onThemeChange("system")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTheme = "system"
                        onThemeChange("system")
                    }
            )
            
            // App Settings
            Text(
                text = "App Settings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            // Show System Apps
            ListItem(
                headlineContent = { Text("Show System Apps") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = showSystemAppsState,
                        onCheckedChange = { 
                            showSystemAppsState = it
                            onShowSystemAppsChange(it)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            
            // Clear Cache
            ListItem(
                headlineContent = { Text("Clear Cache") },
                supportingContent = { Text(formatFileSize(cacheSize)) },
                leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showClearCacheDialog = true
                    }
            )
            
            // About Section
            Text(
                text = "About",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            ListItem(
                headlineContent = { Text("App Share") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                supportingContent = { Text("Version 1.0.1\nABI: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"}") },
                modifier = Modifier.fillMaxWidth()
            )
            
            ListItem(
                headlineContent = { Text("Developed by Hossein Pira") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Developer Links
            ListItem(
                headlineContent = { Text("Telegram") },
                leadingContent = { Icon(Icons.Default.Send, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/h3dev")))
                        } catch (e: Exception) {
                            // Handle error if needed
                        }
                    }
            )
            
            ListItem(
                headlineContent = { Text("Email") },
                leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:h3dev.pira@gmail.com")))
                        } catch (e: Exception) {
                            // Handle error if needed
                        }
                    }
            )
            
            // Exit App Button
            ListItem(
                headlineContent = { Text("Exit App") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showExitDialog = true
                    }
            )
        }
    }
}

/**
 * Format file size in human-readable format
 */
fun formatFileSize(size: Long): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        size >= gb -> String.format("%.2f GB", size / gb)
        size >= mb -> String.format("%.2f MB", size / mb)
        size >= kb -> String.format("%.2f KB", size / kb)
        else -> "$size B"
    }
}