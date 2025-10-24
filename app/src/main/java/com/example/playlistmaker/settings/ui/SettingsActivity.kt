package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Toolbar>(R.id.settings_tb_back)
        val btnShare = findViewById<TextView>(R.id.settings_tv_share)
        val btnSupport = findViewById<TextView>(R.id.settings_tv_support)
        val btnAgreement = findViewById<TextView>(R.id.settings_tv_agreement)
        val swtTheme = findViewById<Switch>(R.id.settings_swt_theme)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(this)).get(SettingsViewModel::class.java)
        viewModel.init()
        viewModel.observeDarkTheme().observe(this) {swtTheme.isChecked = it}
        viewModel.observeErrorState().observe(this) {
            if (!it.isEmpty()) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.getDarkTheme()

        btnBack.setOnClickListener{
            this.finish()
        }

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
            (applicationContext as App).switchTheme(checked)
            viewModel.setDarkTheme(checked)
        }

    }
}