package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksConsumer
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor, private val tracksHistoryInteractor: TracksHistoryInteractor): ViewModel() {

    private var searchJob: Job? = null

    private var searchString = ""
    private var lastQuery = ""


    private val tracksStateLiveData = MutableLiveData<TracksState>(TracksState(false, false, false, "", emptyList<Track>().toMutableList()))
    fun observeTrackState(): LiveData<TracksState> = tracksStateLiveData

    private val tracksHistoryStateLiveData = MutableLiveData<TracksHistoryState>(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
    fun observeTrackHistoryState(): LiveData<TracksHistoryState> = tracksHistoryStateLiveData

    fun searchTextEntered(inputText: String) {
        searchJob?.cancel()
        searchString = inputText
        if (!searchString.isEmpty()) {
            loadTracks()
        } else {
            tracksStateLiveData.postValue(TracksState(false, false, false, "",emptyList<Track>().toMutableList()))
            showHistory()
        }
    }

    fun searchTextChanged(inputText: String) {
        if (searchString == inputText) return
        searchString = inputText
        searchJob?.cancel()
        if (searchString.isEmpty()) {
            tracksStateLiveData.postValue(TracksState(false, false, false, "",emptyList<Track>().toMutableList()))
            showHistory()
        } else {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                loadTracks()
            }
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
        viewModelScope.launch {
            tracksHistoryInteractor.addTrack(track, {})}
    }

    fun clearTracksHistory(){
        tracksHistoryInteractor.clearTracks {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(false, false, emptyList<Track>().toMutableList()))
        }
    }

    private fun showHistory(){
        viewModelScope.launch {
            tracksHistoryInteractor.loadTracks(TracksHistoryConsumerImpl())}
    }

    private fun loadTracks(){
        tracksStateLiveData.postValue(TracksState(true,false, false, "", emptyList<Track>().toMutableList()))
        lastQuery = searchString
        viewModelScope.launch {
            tracksInteractor.loadTracks(searchString).collect { tracks ->
                tracksStateLiveData.postValue(TracksState(
                    false,tracks.isEmpty(), false, "", (tracks?:emptyList()).toMutableList())
                )
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    inner class TracksHistoryConsumerImpl: TracksConsumer{
        override fun consume(tracks: List<Track>) {
            tracksHistoryStateLiveData.postValue(TracksHistoryState(
                tracks.isNotEmpty(),tracks.isEmpty(), (tracks?:emptyList()).toMutableList())
            )
        }
    }

}