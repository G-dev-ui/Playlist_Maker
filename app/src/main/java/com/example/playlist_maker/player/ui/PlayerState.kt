package com.example.playlist_maker.player.ui

interface PlayerState {

    object Prepared: PlayerState
    object Playing: PlayerState
    object Pause: PlayerState
    object Complete: PlayerState

    data class ChangePosition(val position: String): PlayerState
}