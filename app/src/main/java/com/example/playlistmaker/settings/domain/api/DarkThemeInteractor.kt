package com.example.playlistmaker.settings.domain.api

interface DarkThemeInteractor {
    fun get(): Boolean
    fun set(state: Boolean)
}