package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.main.ui.App
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Toolbar>(R.id.settings_tb_back)
        val btnShare = findViewById<TextView>(R.id.settings_tv_share)
        val btnSupport = findViewById<TextView>(R.id.settings_tv_support)
        val btnAgreement = findViewById<TextView>(R.id.settings_tv_agreement)
        val swtTheme = findViewById<Switch>(R.id.settings_swt_theme)

        viewModel.init()
        viewModel.observeDarkTheme().observe(this) {swtTheme.isChecked = it}
        viewModel.observeErrorState().observe(this) {
            if (!it.isEmpty()) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            viewModel.errorSpent()
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