package com.example.radiokazansings

import android.app.Activity
import android.app.Service
import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


private const val AUDIO_URL = "https://av.bimradio.ru/bim_mp3_128k"
// private const val AUDIO_URL = "https://stream01.hitv.ru:8443/kazansings-320kb"

class RadioPlayer(private val context: Context) {

    private var player: ExoPlayer? = null
    private var numberOfActiveClients = 0

    fun attach() {
        numberOfActiveClients++
        if (numberOfActiveClients == 1) {
            initPlayer()
        }
    }

    fun detach() {
        numberOfActiveClients--
        if (numberOfActiveClients == 0) {
            player?.release()
            player = null
        }
    }

    var playWhenReady
        get() = player?.playWhenReady == true
        set(value) {
            player?.playWhenReady = value
        }

    private fun initPlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(AUDIO_URL)
            addMediaItem(mediaItem)
            prepare()
        }

    }

}

fun Activity.isRadioPlaying() = application.getPlayer().playWhenReady

fun Service.isRadioPlaying() = application.getPlayer().playWhenReady

fun Activity.getRadioPlayer() = application.getPlayer()

fun Service.getRadioPlayer() = application.getPlayer()

