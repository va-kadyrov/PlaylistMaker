package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksConsumer

class SearchViewModel(context: Context): ViewModel() {

    val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {searchTracksRunnable() }
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor(context)

    private var searchString = ""
    private var lastQuery = ""


    private val tracksStateLiveData = MutableLiveData<TracksState>(TracksState(false, false, false, "", emptyList<Track>().toMutableList()))
    fun observeTrackState(): LiveData<TracksState> = tracksStateLiveData

    private val tracksHistoryStateLiveData = MutableLiveData<TracksHistoryState>(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
    fun observeTrackHistoryState(): LiveData<TracksHistoryState> = tracksHistoryStateLiveData

    fun searchTextEntered(inputText: String) {
        searchString = inputText
        if (!searchString.isEmpty()) {
            loadTracks()
        } else {
            tracksStateLiveData.postValue(TracksState(false, false, false, "",emptyList<Track>().toMutableList()))
            showHistory()
        }
    }

    fun searchTextChanged(inputText: String) {
        searchString = inputText
        handler.removeCallbacks(searchRunnable)
        if (searchString.isEmpty()) {
            tracksStateLiveData.postValue(TracksState(false, false, false, "",emptyList<Track>().toMutableList()))
            showHistory()
        } else {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun searchFielsOnFocus() {
        if (searchString.isEmpty()) {
            showHistory()
        }
    }

    fun repeatSearch() {
        if (!searchString.isEmpty()) {
            loadTracks()
        }
    }

    fun addTrackToHistory(track: Track){
        tracksHistoryInteractor.addTrack(track, {})
    }

    fun clearTracksHistory(){
        tracksHistoryInteractor.clearTracks {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
        }
    }

    private fun showHistory(){
        tracksHistoryInteractor.loadTracks(TracksHistoryConsumerImpl())
    }

    private fun loadTracks(){
        tracksStateLiveData.postValue(TracksState(true,false, false, "", emptyList<Track>().toMutableList()))
        lastQuery = searchString
        tracksInteractor.loadTracks(searchString, TracksConsumerImpl())
    }

    private fun searchTracksRunnable() {
        if (searchString.isNotEmpty()) {
            loadTracks()
        }
    }

    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory = viewModelFactory{
            initializer {
                SearchViewModel(context)
            }
        }
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    inner class TracksConsumerImpl: TracksConsumer {
        override fun consume(tracks: List<Track>) {
            tracksStateLiveData.postValue(TracksState(
                    false,tracks.isEmpty(), false, "", (tracks?:emptyList()).toMutableList())
                )
        }
    }

    inner class TracksHistoryConsumerImpl: TracksConsumer{
        override fun consume(tracks: List<Track>) {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(
                tracks.isNotEmpty(),tracks.isEmpty(), (tracks?:emptyList()).toMutableList())
            )
        }
    }

}