package com.pira.appshare.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var themePreference: String
        get() = prefs.getString(THEME_PREF_KEY, "system") ?: "system"
        set(value) = prefs.edit().putString(THEME_PREF_KEY, value).apply()
        
    var showSystemApps: Boolean
        get() = prefs.getBoolean(SHOW_SYSTEM_APPS_KEY, false)
        set(value) = prefs.edit().putBoolean(SHOW_SYSTEM_APPS_KEY, value).apply()
    
    companion object {
        private const val PREFS_NAME = "app_share_prefs"
        private const val THEME_PREF_KEY = "theme_preference"
        private const val SHOW_SYSTEM_APPS_KEY = "show_system_apps"
        
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        
        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}