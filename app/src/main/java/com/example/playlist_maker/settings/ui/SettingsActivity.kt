package com.example.playlist_maker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.App
import com.example.playlist_maker.R
import com.example.playlist_maker.player.ui.MediaPlayerViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsViewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]
        settingsViewModel.getThemeLiveData().observe(this){ isChecked ->
            themeSwitcher.isChecked = isChecked
        }

         themeSwitcher = findViewById(R.id.theme_switch)
        val shareButton = findViewById<ImageView>(R.id.share_the_application)
        val supportButton = findViewById<ImageView>(R.id.write_to_support)
        val agreementButton = findViewById<ImageView>(R.id.user_agreement)
        val backButton = findViewById<ImageView>(R.id.image)


        backButton.setOnClickListener {
            finish()
        }

        supportButton.setOnClickListener{
            settingsViewModel.OpenSupport()
        }

        shareButton.setOnClickListener {
            settingsViewModel.ShareApp()
        }

        agreementButton.setOnClickListener {
            settingsViewModel.OpenTerms()
        }

        themeSwitcher.setOnCheckedChangeListener{ switcher, isChecked ->
            settingsViewModel.clickSwitchTheme(isChecked)
        }

    }

}
