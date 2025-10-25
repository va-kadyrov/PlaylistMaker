package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository

const val DARK_THEME = "DARK_THEME"

class DarkThemeInteractorImpl(private val repository: PropBooleanRepository): DarkThemeInteractor {
    override fun get(): Boolean {
        return repository.get(DARK_THEME)
    }

    override fun set(state: Boolean) {
        repository.set(DARK_THEME, state)
    }
}