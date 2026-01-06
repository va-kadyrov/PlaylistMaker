package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TracksHistoryRepositoryImpl (private val sharedPreferences: SharedPreferences, private val appDatabase: AppDatabase) : TracksHistoryRepository {

    override suspend fun loadTracks(): List<Track> {
        val favoriteTracksIds = withContext(Dispatchers.IO) { appDatabase.trackDao().getIds() }
        try{
            val json = sharedPreferences.getString(PREF_KEY, "")
            val tracks = Gson().fromJson(json, Array<Track>::class.java).toMutableList()
            tracks.forEach {track -> track.isFavorite = favoriteTracksIds.contains(track.trackId)}
            return tracks
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