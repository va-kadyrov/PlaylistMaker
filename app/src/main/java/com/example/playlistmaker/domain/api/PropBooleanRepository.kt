package com.example.playlistmaker.domain.api

interface PropBooleanRepository {
    fun get(propName: String): Boolean
    fun set(propName: String, state: Boolean)
}