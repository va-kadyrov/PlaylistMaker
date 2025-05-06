package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.SearchActivity.Companion.PM_SHARED_PREFERENCES

class App : Application () {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreference = getSharedPreferences(PM_SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreference.getBoolean(DARK_THEME, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}