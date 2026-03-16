package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.TAG
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onResume(){
        super.onResume()
        viewModel.repeatSearch()
        viewModel.searchFielsOnFocus()
    }

    override fun onCreateView(
        inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    viewModel.observeTrackState(),
                    viewModel.observeTrackHistoryState(),
                    viewModel::searchTextEntered,
                    viewModel::searchTextChanged,
                    viewModel::repeatSearch,
                    viewModel::addTrackToHistory,
                    viewModel::clearTracksHistory,
                    viewModel::searchFielsOnFocus,
                    ::openPlayer
                )
            }

    }}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

        fun openPlayer(track: Track) {
            val gson: Gson by inject()
            val trackJson: String = gson.toJson(track)
            Log.i(TAG, "Player are opening")
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
        }

}


