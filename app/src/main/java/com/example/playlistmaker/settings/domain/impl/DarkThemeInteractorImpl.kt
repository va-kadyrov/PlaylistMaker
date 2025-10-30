package com.example.playlistmaker.settings.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository

const val DARK_THEME = "DARK_THEME"

class DarkThemeInteractorImpl(private val repository: PropBooleanRepository): DarkThemeInteractor {
    override fun get(): Boolean {
        return repository.get(DARK_THEME)
    }

    override fun set(state: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (state) {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            })

        repository.set(DARK_THEME, state)
    }
}