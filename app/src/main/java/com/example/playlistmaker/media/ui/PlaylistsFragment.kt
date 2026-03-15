package com.example.playlistmaker.media.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
//import com.example.playlistmaker.search.ui.SearchFragment.TracksViewHolder
import com.example.playlistmaker.search.ui.TracksState
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Date
import kotlin.getValue

class PlaylistsFragment : Fragment() {

    val viewModel by activityViewModel<PlaylistsViewModel>()

    override fun onResume(){
        super.onResume()
        viewModel.loadPlaylists()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply{
            setContent {
                PlaylistsScreen(
                    viewModel.observePlaylistsState(),
                    {
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_newPlaylistFragment)
                    },
                    { playlistId ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistInfoFragment,
                            PlaylistInfoFragment.createArgs(playlistId))
                    }
                )
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}