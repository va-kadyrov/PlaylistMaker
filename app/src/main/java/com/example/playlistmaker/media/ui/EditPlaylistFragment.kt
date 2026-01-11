package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.android.ext.android.inject

class EditPlaylistFragment: NewPlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by inject()
    private var playlistId: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newPlaylistBack         = binding.newPlaylistBack
        val newPlaylistBtn          = binding.newPlaylistBtn
        val newPlaylistName         = binding.newPlaylistName
        val newPlaylistDescription  = binding.newPlaylistDescription
        val newPlaylistImageview    = binding.newPlaylistImageview

        playlistId = requireArguments().getLong(PLAYLIST_ID) ?: 0

        newPlaylistBtn.setText("Сохранить")
        newPlaylistBack.setTitle("Редактировать данные")

        viewModel.loadPlaylistInfo(playlistId)
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            if (it != null) {
                newPlaylistName.editText?.setText(it.name)
                newPlaylistDescription.editText?.setText(it.description)
                newPlaylistName.editText?.setText(it.name)
                if (it.filePath.isNotEmpty()) {
                    newPlaylistImageview.setImageURI(it.filePath.toUri())
                }
            }
        }

        newPlaylistBack.setOnClickListener {
            findNavController().navigateUp()
        }

        newPlaylistBtn.setOnClickListener {
            viewModel.savePlaylist()
        }

    }

    override val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }

    override fun toastCreatePlaylist() {
        Toast.makeText(
            requireActivity(),
            "Плейлист ${viewModel.playlistName} сохранен",
            Toast.LENGTH_SHORT
        ).show()
    }


    companion object {
        private const val PLAYLIST_ID = "plailist_id"
        fun createArgs(plailistid: Long) : Bundle = bundleOf(PLAYLIST_ID to plailistid)
    }
}
