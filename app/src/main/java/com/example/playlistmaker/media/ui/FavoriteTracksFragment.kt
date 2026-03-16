package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.TAG
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.getValue

class FavoriteTracksFragment : Fragment() {

    val viewModel by activityViewModel<FavoriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setContent {
                FavoriteTracksScreen(
                    viewModel.observeFavoriteTrackState(),
                    ::openPlayer
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFavoriteTracks()
    }

    fun openPlayer(track: Track) {
        val gson: Gson by inject()
        val trackJson: String = gson.toJson(track)
        Log.i(TAG, "Player are opening")
        findNavController().navigate(
            R.id.action_mediaFragment_to_playerFragment,
            PlayerFragment.createArgs(trackJson)
        )
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

}


