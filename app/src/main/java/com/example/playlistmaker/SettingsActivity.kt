package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Toolbar>(R.id.settings_tb_back)
        val btnShare = findViewById<TextView>(R.id.settings_tv_share)
        val btnSupport = findViewById<TextView>(R.id.settings_tv_support)
        val btnAgreement = findViewById<TextView>(R.id.settings_tv_agreement)

        btnBack.setOnClickListener{
            this.finish()
        }

        btnShare.setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ref_yandex_practicum_android))
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, null))
        }

        btnSupport.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra("EXTRA_TEXT", getString(R.string.ref_yandex_practicum))
            //if(supportIntent.resolveActivity(getPackageManager()) == null) {
            try {
                startActivity(supportIntent)
            }catch(e: Exception){
                Toast.makeText(this, "${getString(R.string.err_start_activity)} отправки почты: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        btnAgreement.setOnClickListener{
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.ref_yandex_practicum_offer))
            try{
                startActivity(agreementIntent)
            }catch(e: Exception){
                Toast.makeText(this, "${getString(R.string.err_start_activity)} открытия веб-страницы: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }

    }
}