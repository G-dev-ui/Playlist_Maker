package com.example.playlist_maker.settings.domain

import com.example.playlist_maker.settings.data.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(isChecked: Boolean) {
        val themeSettings = ThemeSettings(isChecked)
        return settingsRepository.updateThemeSetting(themeSettings)
    }


}