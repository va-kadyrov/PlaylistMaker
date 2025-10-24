package com.example.playlistmaker.main.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator.provideDarkThemeInteractor

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val darkThemeInteractor = provideDarkThemeInteractor(getApplicationContext())

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