package com.example.radiokazansings

import android.app.Activity
import android.app.Service
import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class RadioPlayer(private val context: Context) {
    private var player: ExoPlayer? = null
    private var numberOfActiveClients = 0

    private var playerConfig = PlayerConfig(
        url64 = "https://stream01.hitv.ru:8443/kazansings-64kb",
        url128 = "https://stream01.hitv.ru:8443/kazansings-128kb",
        url192 = "https://stream01.hitv.ru:8443/kazansings-192kb",
        url320 = "https://stream01.hitv.ru:8443/kazansings-320kb"
    )

    var bitrate = Bitrate.bitrate320
        set(value) {
            val isPlaying = player?.isPlaying == true
            field = value
            if (player != null) {
                releasePlayer()
                initPlayer()
            }
            if (isPlaying) {
                player?.playWhenReady = true
            }
        }

    private fun getAudioUrl() = when (bitrate) {
        Bitrate.bitrate64 -> playerConfig.url64
        Bitrate.bitrate128 -> playerConfig.url128
        Bitrate.bitrate192 -> playerConfig.url192
        Bitrate.bitrate320 -> playerConfig.url320
    }

    fun attach() {
        numberOfActiveClients++
        if (numberOfActiveClients == 1) {
            Log.d("RemoteConfigUtils", "player is going to start")
            initPlayer()
        }
    }

    fun detach() {
        numberOfActiveClients--
        if (numberOfActiveClients == 0) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    var playWhenReady
        get() = player?.playWhenReady == true
        set(value) {
            player?.playWhenReady = value
        }

    private fun initPlayer() {
        player = ExoPlayer.Builder(context).build().apply {
            initWithNewUrl()
        }

    }

    private fun ExoPlayer.initWithNewUrl() {
        val mediaItem = MediaItem.fromUri(getAudioUrl())
        addMediaItem(mediaItem)
        prepare()
    }

    fun onPlayerConfigUpdated(playerConfig: PlayerConfig) {
        val isPlaying = player?.playWhenReady == true
        val currentUrl = getAudioUrl()
        this.playerConfig = playerConfig
        val newUrl = getAudioUrl()
        if (currentUrl != newUrl) {
            if (player != null) {
                releasePlayer()
                initPlayer()
            }
            if (isPlaying) {
                player?.playWhenReady = true
            }
        }
    }

}

data class PlayerConfig(
    val url64: String,
    val url128: String,
    val url192: String,
    val url320: String,
)

enum class Bitrate {
    bitrate64,
    bitrate128,
    bitrate192,
    bitrate320
}

fun Activity.isRadioPlaying() = application.getPlayer().playWhenReady

fun Service.isRadioPlaying() = application.getPlayer().playWhenReady

fun Activity.getRadioPlayer() = application.getPlayer()

fun Service.getRadioPlayer() = application.getPlayer()

