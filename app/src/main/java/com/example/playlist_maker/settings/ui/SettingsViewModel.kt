package com.example.playlist_maker.settings.ui


import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import com.example.playlist_maker.settings.domain.SettingsInteractor
import com.example.playlist_maker.sharing.domain.SharingInteractor

class SettingsViewModel (private val sharingInteractor: SharingInteractor,
                         private val settingsInteractor: SettingsInteractor,
) : ViewModel() {


    private var themeLiveData = MutableLiveData<Boolean>()

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().darkTheme
    }

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData


    fun ShareApp(){
 sharingInteractor.shareApp()
    }

    fun OpenSupport(){
sharingInteractor.openSupport()
    }

    fun OpenTerms(){
sharingInteractor.openTerms()
    }

    fun clickSwitchTheme(isChecked: Boolean) {
        Log.d("TEST", "clickSwitchTheme")
        themeLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(isChecked)
        applyTheme(isChecked)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        Log.d("TEST", "applyTheme")
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}
