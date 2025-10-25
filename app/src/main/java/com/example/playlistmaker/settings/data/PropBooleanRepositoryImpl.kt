package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository

class PropBooleanRepositoryImpl(private val sharedPreference: SharedPreferences):
    PropBooleanRepository {

    override fun get(propName: String): Boolean {
        return sharedPreference.getBoolean(propName, false)
    }

    override fun set(propName: String, state: Boolean) {
        sharedPreference.edit().putBoolean(propName, state).apply()
    }
}
