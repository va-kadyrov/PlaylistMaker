package com.example.playlistmaker.search.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.google.gson.Gson

class TracksHistoryRepositoryImpl (private val context: Context, private val sharedPreferences: SharedPreferences) : TracksHistoryRepository {

    override fun loadTracks(): List<Track> {
        try{
            val json = sharedPreferences.getString(PREF_KEY, "")
            return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        } catch (e: Exception){
            e.printStackTrace()
            return emptyList()
        }
    }

    override fun saveTracks(tracks: List<Track>){
        try{
            val json = Gson().toJson(tracks)
            sharedPreferences.edit().putString(PREF_KEY, json).apply()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    companion object {
        const val PREF_KEY = "SEARCH_HISTORY"
    }

}