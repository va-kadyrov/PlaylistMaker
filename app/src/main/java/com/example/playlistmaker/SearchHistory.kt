package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.SearchActivity.Companion.TAG
import com.google.gson.Gson

const val PREF_KEY = "SEARCH_HISTORY"

class SearchHistory{

    val tracks= mutableListOf<Track>()
    lateinit var sharedPreference: SharedPreferences //Не могу придумать нормальную реализацию областей видимости. Поэтому делаю sharedPreference variable

    fun addTrack(track: Track){
        Log.d(TAG, "adding track.trackName: ${track.trackName}")
        for (eachTrack in tracks){
            if (eachTrack.trackId == track.trackId)
            {
                Log.d(TAG, "removing eachTrack.trackName: ${eachTrack.trackName}")
                tracks.remove(eachTrack)
                break
            }
        }
        tracks.add(0, track)
        if (tracks.size > 10)
        {
            Log.d(TAG, "removing eachTrack.trackName: ${tracks[tracks.size-1].trackName}")
            tracks.removeAt(tracks.size-1)
        }
        saveTracks()
    }

    private fun saveTracks(){
        try{
            val json = Gson().toJson(tracks)
            sharedPreference.edit().putString(PREF_KEY, json).apply()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun loadTracks(){
        try{
            val json = sharedPreference.getString(PREF_KEY, "")
            val tracksArray = Gson().fromJson(json, Array<Track>::class.java)
            tracks.clear()
            for (track in tracksArray) tracks.add(track)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun clearTracks(){
        tracks.clear()
        saveTracks()
    }
}