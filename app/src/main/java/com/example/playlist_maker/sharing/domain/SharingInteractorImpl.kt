package com.example.playlist_maker.sharing.domain

import com.example.playlist_maker.sharing.data.ExternalNavigator

class SharingInteractorImpl (
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink()
    }

    override fun openTerms() {
        externalNavigator.openLink()
    }

    override fun openSupport() {
        externalNavigator.openEmail()
    }

}