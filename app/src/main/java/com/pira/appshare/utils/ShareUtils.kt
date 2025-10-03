package com.pira.appshare.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object ShareUtils {
    
    fun shareAppApk(context: Context, packageName: String) {
        // Move the heavy operation to background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(packageName, 0)
                }
                
                val applicationInfo = packageInfo.applicationInfo ?: return@launch
                
                val sourceApk = File(applicationInfo.sourceDir)
                
                // Create a temporary file to copy the APK to
                val tempDir = File(context.cacheDir, "apks")
                if (!tempDir.exists()) {
                    tempDir.mkdirs()
                }
                
                val targetApk = File(tempDir, "${packageName}.apk")
                if (targetApk.exists()) {
                    targetApk.delete()
                }
                
                // Copy the APK file
                sourceApk.copyTo(targetApk)
                
                // Share the APK on the main thread
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            targetApk
                        )
                        
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "application/vnd.android.package-archive"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        
                        context.startActivity(Intent.createChooser(shareIntent, "Share APK via"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed to share APK: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Show error on main thread
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Failed to extract APK: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}