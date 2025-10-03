package com.pira.appshare.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.pira.appshare.model.AppInfo

object AppUtils {
    
    fun getInstalledApps(context: Context, showSystemApps: Boolean = false): List<AppInfo> {
        val packageManager = context.packageManager
        val apps = mutableListOf<AppInfo>()
        
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
        
        for (packageInfo in packages) {
            val applicationInfo = packageInfo.applicationInfo ?: continue
            
            // Skip system apps if not enabled
            if (!showSystemApps && applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                continue
            }
            
            val name = applicationInfo.loadLabel(packageManager).toString()
            val packageName = packageInfo.packageName
            val versionName = packageInfo.versionName ?: "Unknown"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
            
            val icon: Drawable = try {
                applicationInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                packageManager.getDefaultActivityIcon()
            }
            
            apps.add(AppInfo(name, packageName, versionName, versionCode, icon))
        }
        
        // Sort apps by name
        return apps.sortedBy { it.name }
    }
}