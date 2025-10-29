package com.example.playlistmaker.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.api.DarkThemeInteractor

class SettingsViewModel (private val context: Context, private val darkThemeInteractor: DarkThemeInteractor): ViewModel() {

    private val darkThemeState = MutableLiveData<Boolean>()
    fun observeDarkTheme(): LiveData<Boolean> = darkThemeState

    private val errorState = MutableLiveData<String>()
    fun observeErrorState(): LiveData<String> = errorState

    fun init(){
        errorState.postValue("")
    }

    fun errorSpent(){
        errorState.postValue("")
    }

    fun getDarkTheme(): Boolean {
        return darkThemeInteractor.get()
    }

    fun setDarkTheme(state: Boolean) {
        if (state != darkThemeInteractor.get()){
            darkThemeInteractor.set(state)}
        if (state != darkThemeState.value){
                     darkThemeState.postValue(state)
            }
    }

    fun share(){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.ref_yandex_practicum_android))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.type = "text/plain"
        try {
            context.startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: Exception) {
            errorState.postValue("${context.getString(R.string.err_start_activity)} открытия веб-страницы: ${e.message}")
        }
    }

    fun support() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.share_email)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_title))
        supportIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(supportIntent)
        } catch (e: Exception) {
            errorState.postValue("${context.getString(R.string.err_start_activity)} отправки почты: ${e.message}")
        }
    }

    fun agreement(){
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(context.getString(R.string.ref_yandex_practicum_offer))
        agreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try{
            context.startActivity(agreementIntent)
        }catch(e: Exception){
            errorState.postValue("${context.getString(R.string.err_start_activity)} открытия веб-страницы: ${e.message}")
        }
    }

}

