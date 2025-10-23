package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator.provideDarkThemeInteractor

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreference = getSharedPreferences(PM_SHARED_PREFERENCES, MODE_PRIVATE)
        val darkThemeInteractor = provideDarkThemeInteractor(sharedPreference)

        darkTheme = darkThemeInteractor.get()
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
    companion object {
        const val PM_SHARED_PREFERENCES = "PM_SHARED_PREFERENCES"
    }
}