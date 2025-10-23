package com.example.playlistmaker.domain.api

interface DarkThemeInteractor {
    fun get(): Boolean
    fun set(state: Boolean)
}