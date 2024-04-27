package com.example.playlist_maker.settings.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {


    private lateinit var themeSwitcher: SwitchMaterial

    private val settingsViewModel by  viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


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
