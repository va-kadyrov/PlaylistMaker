package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
//import com.example.playlistmaker.main.ui.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnShare = binding.settingsTvShare
        val btnSupport = binding.settingsTvSupport
        val btnAgreement = binding.settingsTvAgreement
        val swtTheme = binding.settingsSwtTheme

        viewModel.init()
        viewModel.observeDarkTheme().observe(viewLifecycleOwner) {
//            if (swtTheme.isChecked != it)
//                swtTheme.isChecked = it
        }
        viewModel.observeErrorState().observe(viewLifecycleOwner) {
            if (!it.isEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.errorSpent()
            }
        }

        swtTheme.isChecked = viewModel.getDarkTheme()

        btnShare.setOnClickListener{
            viewModel.share()
        }

        btnSupport.setOnClickListener{
            viewModel.support()
        }

        btnAgreement.setOnClickListener{
            viewModel.agreement()
        }

        swtTheme.setOnCheckedChangeListener { switcher, checked ->
//            (activity?.applicationContext as App).switchTheme(checked)
            viewModel.setDarkTheme(checked)
        }
    }
}