package com.example.radiokazansings

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.radiokazansings.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*

class MainActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var player: ExoPlayer
    private lateinit var titleSongs: TextView
    private lateinit var customToolbar: Toolbar

    var audioUrl = "https://stream01.hitv.ru:8443/kazansings-320kb"

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
                R.id.item_64kb -> {
                    Toast.makeText(this, "64kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_128kb -> {
                    Toast.makeText(this, "128kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_192kb -> {
                    Toast.makeText(this, "192kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_320kb -> {
                    Toast.makeText(this, "320kb", Toast.LENGTH_SHORT).show()
                }
                R.id.share -> {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    // type of the content to be shared
                    sharingIntent.type = "text/plain"
                    // Body of the content
                    val shareBody = getString(R.string.share_body)
                    // subject of the content. you can share anything
                    val shareSubject = R.drawable.logo
                    // passing body of the content
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    // passing subject of the content
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    startActivity(Intent.createChooser(sharingIntent, "Share using"))
                }
                R.id.aboutUs -> {
                    val intent = Intent(this, Contacts::class.java)
                    startActivity(intent)
                }
            }
            return@setOnMenuItemClickListener true
        }

        setUpPlayer()
        addStreamUrl()

        binding.imPlay.setOnClickListener {
            player.playWhenReady = !player.playWhenReady
            binding.tvSongs.text = if (player.playWhenReady) "Is Playing" else "Paused"
            binding.imPlay.setImageResource(
                if (player.playWhenReady) {
                    R.drawable.image_pause
                } else {
                    R.drawable.image_play
                }
            )
        }

        binding.ibLike.setOnClickListener{
            Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show()
        }
        binding.ibDislike.setOnClickListener {
            Toast.makeText(this, "Dislike", Toast.LENGTH_SHORT).show()
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


