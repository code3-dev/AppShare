package com.pira.appshare.ui

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pira.appshare.R
import com.pira.appshare.data.PreferencesManager
import com.pira.appshare.model.AppInfo
import com.pira.appshare.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AppListScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val preferencesManager = remember { PreferencesManager.getInstance(context) }
    
    // Permission handling
    val queryAllPackagesPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rememberPermissionState(Manifest.permission.QUERY_ALL_PACKAGES)
    } else {
        null
    }
    
    var apps by remember { mutableStateOf<List<AppInfo>?>(null) }
    var filteredApps by remember { mutableStateOf<List<AppInfo>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showSystemApps by remember { mutableStateOf(preferencesManager.showSystemApps) }
    
    LaunchedEffect(Unit) {
        // Check if we have the necessary permissions
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            queryAllPackagesPermissionState?.status?.isGranted ?: true
        } else {
            true
        }
        
        if (hasPermission) {
            // Move the heavy operation to background thread
            withContext(Dispatchers.IO) {
                val appList = AppUtils.getInstalledApps(context, showSystemApps)
                withContext(Dispatchers.Main) {
                    apps = appList
                    filteredApps = appList
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }
    
    // Reload apps when showSystemApps changes
    LaunchedEffect(showSystemApps) {
        preferencesManager.showSystemApps = showSystemApps
        if (!isLoading) {
            withContext(Dispatchers.IO) {
                val appList = AppUtils.getInstalledApps(context, showSystemApps)
                withContext(Dispatchers.Main) {
                    apps = appList
                    filteredApps = appList
                }
            }
        }
    }
    
    // Filter apps based on search query
    LaunchedEffect(searchQuery, apps) {
        filteredApps = if (searchQuery.isBlank()) {
            apps
        } else {
            apps?.filter { appInfo: AppInfo ->
                appInfo.name.contains(searchQuery, ignoreCase = true) ||
                appInfo.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
        ) {
            // Simple search input field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search apps") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                }
            )
            
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    apps == null -> {
                        // Handle permission denied case
                        Text(
                            text = "Permission required to view apps",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            filteredApps?.let { appList ->
                                if (appList.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No apps found",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.Center)
                                        )
                                    }
                                } else {
                                    items(appList) { appInfo ->
                                        AppItem(appInfo = appInfo)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}