package com.example.radiokazansings

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.radiokazansings.databinding.ActivityMainBinding
import com.google.android.exoplayer2.Player

class MainActivity : AppCompatActivity(), Player.Listener {
    lateinit var binding: ActivityMainBinding
    private lateinit var titleSongs: TextView
    private lateinit var customToolbar: Toolbar

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val playButtonStatusBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updatePlayButton()
        }

    }

    @RequiresApi(33)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleSongs = binding.tvSongs
        customToolbar = binding.customToolbar
        customToolbar.inflateMenu(R.menu.bitrate_menu)
        customToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_64kb -> {
                    getRadioPlayer().bitrate = Bitrate.bitrate64
                    Toast.makeText(this, "64kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_128kb -> {
                    getRadioPlayer().bitrate = Bitrate.bitrate128
                    Toast.makeText(this, "128kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_192kb -> {
                    getRadioPlayer().bitrate = Bitrate.bitrate192
                    Toast.makeText(this, "192kb", Toast.LENGTH_SHORT).show()
                }
                R.id.item_320kb -> {
                    getRadioPlayer().bitrate = Bitrate.bitrate320
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
                    val intent = Intent(this, ContactsActivity::class.java)
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
            //throw RuntimeException("Test Crash")
            updatePlayButton()
        }

        binding.ibLike.setOnClickListener {
            Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show()
        }
        binding.ibInfo.setOnClickListener {
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
        }
        registerReceiver(
            playButtonStatusBroadcastReceiver,
            IntentFilter(ACTION_UPDATE_PLAYER_BUTTONS)
        )
        checkShowPermissionDialog()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkShowPermissionDialog() {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        AlertDialog.Builder(this)
            .setMessage("Разрешить показ плеера Казань поет как уведомление?")
            .setPositiveButton("Да") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Отмена") { _, _ ->

            }.show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updatePlayButton()
    }
}

