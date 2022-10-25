package com.example.radiokazansings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.radiokazansings.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var titleSongs: TextView

    var audioUrl = "https://av.bimradio.ru/bim_mp3_128k"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleSongs = binding.tvSongs

        setUpPlayer()
        addStreamUrl()



        binding.imPlay.setOnClickListener{
            val isPlayWhenReady = player.playWhenReady
            Log.d("MainActivity::", "isPlayWhenReady = $isPlayWhenReady")
            player.playWhenReady = !player.playWhenReady
            binding.imPlay.setImageResource(if (player.playWhenReady) {
                    R.drawable.image_pause
                } else {
                    R.drawable.image_play
                }
            )
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
        playerView = binding.PlayView
        playerView.player = player
        player.addListener(this)

    }

    private fun addStreamUrl(){
        val mediaItem = MediaItem.fromUri(audioUrl)
        player.addMediaItem(mediaItem)
        player.prepare()
        player.play()
        player.playWhenReady = true
    }

    override fun onStop(){
        super.onStop()
       // player.release()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        titleSongs.text = ("is playing")

    }

 // Implement ToolBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bitrate_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
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
        return true
    }

}


