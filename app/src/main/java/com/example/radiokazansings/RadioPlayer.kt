package com.example.radiokazansings

import android.app.Activity
import android.app.Service
import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


private const val AUDIO_URL = "https://av.bimradio.ru/bim_mp3_128k"
// private const val AUDIO_URL = "https://stream01.hitv.ru:8443/kazansings-320kb"

class RadioPlayer(context: Context) {

    private val player: ExoPlayer

    init {
        player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.fromUri(AUDIO_URL)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    var playWhenReady
        get() = player.playWhenReady
        set(value) {
            player.playWhenReady = value
        }

}

fun Activity.isRadioPlaying() = application.getPlayer().playWhenReady

fun Service.isRadioPlaying() = application.getPlayer().playWhenReady

fun Activity.getRadioPlayer() = application.getPlayer()

fun Service.getRadioPlayer() = application.getPlayer()

