package com.example.radiokazansings

import android.app.Application

class KazanRadioApplication: Application() {
    private lateinit var player: RadioPlayer

    override fun onCreate() {
        super.onCreate()
        player = RadioPlayer(this)
    }

    internal fun getPlayer(): RadioPlayer {
        return player
    }
}

fun Application.getPlayer() = (this as KazanRadioApplication).getPlayer()
