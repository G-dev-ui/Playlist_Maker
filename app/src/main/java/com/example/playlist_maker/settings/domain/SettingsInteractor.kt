package com.example.playlist_maker.settings.domain

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(isChecked: Boolean)
}