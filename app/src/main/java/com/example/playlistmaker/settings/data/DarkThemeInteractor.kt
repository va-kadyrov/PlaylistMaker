package com.example.playlistmaker.settings.data

interface DarkThemeInteractor {
    fun get(): Boolean
    fun set(state: Boolean)
}