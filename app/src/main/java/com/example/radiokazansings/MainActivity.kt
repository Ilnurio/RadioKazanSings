package com.example.radiokazansings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.radiokazansings.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var titleSongs: TextView
    private lateinit var customToolbar: Toolbar

    var audioUrl = "https://av.bimradio.ru/bim_mp3_128k"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleSongs = binding.tvSongs
        customToolbar = binding.customToolbar
        customToolbar.inflateMenu(R.menu.bitrate_menu)
        customToolbar.setOnMenuItemClickListener{
            when (it.itemId){
                R.id._64kb -> {
                    Toast.makeText(this, "64kb", Toast.LENGTH_SHORT).show()
                }
                R.id._128kb -> {
                    Toast.makeText(this, "128kb", Toast.LENGTH_SHORT).show()
                }
                R.id._192kb -> {
                    Toast.makeText(this, "192kb", Toast.LENGTH_SHORT).show()
                }
                R.id._320kb -> {
                    Toast.makeText(this, "320kb", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnMenuItemClickListener true
        }

        setUpPlayer()
        addStreamUrl()

        binding.playView.setOnClickListener{
            player.play()
            binding.tvSongs.text = "is Playing"
        }

        binding.playView.setOnClickListener{
            player.stop()
            binding.tvSongs.text = "Pause"
        }

        if (savedInstanceState != null){
            savedInstanceState.getInt("MediaItem").let { restoredMedia ->
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMedia, seekTime)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime", player.currentPosition)
        outState.putInt("MediaItem", player.currentMediaItemIndex)
    }

    private fun setUpPlayer(){
        player = ExoPlayer.Builder(this).build()
        playerView = binding.playView
        playerView.player = player
        player.addListener(this)
    }

    private fun addStreamUrl(){
        val mediaItem = MediaItem.fromUri(audioUrl)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
    }

}


