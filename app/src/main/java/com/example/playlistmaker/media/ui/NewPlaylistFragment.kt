package com.example.playlistmaker.media.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddingPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNewPlaylistBinding
    private val viewModel: NewPlaylistViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newPlaylistBtn          = binding.newPlaylistBtn
        val newPlaylistName         = binding.newPlaylistName
        val newPlaylistDescription  = binding.newPlaylistDescription
        val newPlaylistImageview    = binding.newPlaylistImageview

        val newFileName = "cover_${UUID.randomUUID()}"
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    saveImageToPrivateStorage(uri, newFileName)
                    Log.d("myTag", "image file saved to $uri")
                } else {
                    Log.d("myTag", "No image selected")
                }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel.observeNewPlaylistState().observe(viewLifecycleOwner){
            newPlaylistBtn.isEnabled = it.canBeSaved
            if (it.playlistSaved) {
                Toast.makeText(requireActivity(), "Плейлист ${viewModel.playlistName} создан", Toast.LENGTH_SHORT).show()
                 findNavController().popBackStack()
            }
        }

        newPlaylistImageview.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))}

        newPlaylistBtn.isEnabled = false //кнопка доступна только после ввода названия плейлиста
        newPlaylistBtn.setOnClickListener {
            viewModel.savePlaylist()
        }

        newPlaylistName.editText?.doOnTextChanged { inputText, _, _, _ ->
            viewModel.playlistName(inputText.toString())
        }

        newPlaylistDescription.editText?.doOnTextChanged { inputText, _, _, _ ->
            viewModel.playlistDescription(inputText.toString())
        }

    }

    private fun saveImageToPrivateStorage(uri: Uri, newFileName:String) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.covers_path))
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val newFile = File(filePath, newFileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(newFile)
        outputStream.write(inputStream?.readBytes())
        binding.newPlaylistImageview.setImageURI(newFile.toUri())
        Log.d("myTag", "filepath = "+newFile.toUri().toString())
        viewModel.playlistFilepath(newFile.toUri().toString())
    }

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showWarningToast()
        }
    }

    private fun showWarningToast() {
        Toast.makeText(requireActivity(), "Нажмите ещё раз, чтобы перейти на предыдущий экран", Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddingPlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}