package com.example.radiokazansings

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.radiokazansings.databinding.ActivityMainBinding
import com.google.android.exoplayer2.*

class MainActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var titleSongs: TextView
    private lateinit var customToolbar: Toolbar
    //var audioUrl = "https://stream01.hitv.ru:8443/kazansings-320kb"

    private val playButtonStatusBroadcastReceiver = object :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updatePlayButton()
        }

    }

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
                    val shareSubject = R.drawable.logo_new
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

        updatePlayButton()

        binding.imPlay.setOnClickListener {
            getRadioPlayer().playWhenReady = !getRadioPlayer().playWhenReady
            ForegroundService.startService(this, "Is playing")
            titleSongs.text = if (isRadioPlaying()) "Is Playing" else "Is Paused"
            updatePlayButton()
        }

        binding.ibLike.setOnClickListener{
            Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show()
        }
        binding.ibInfo.setOnClickListener {
            val intent = Intent(this, Information::class.java)
            startActivity(intent)
        }
        registerReceiver(playButtonStatusBroadcastReceiver, IntentFilter(ACTION_UPDATE_PLAYER_BUTTONS))
    }

    override fun onStart() {
        super.onStart()
        getRadioPlayer().attach()
    }

    override fun onStop() {
        super.onStop()
        getRadioPlayer().detach()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playButtonStatusBroadcastReceiver)
    }

    private fun updatePlayButton() {
        binding.imPlay.setImageResource(
            if (isRadioPlaying()) {
                R.drawable.image_pause
            } else {
                R.drawable.image_play
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updatePlayButton()
    }
}

