package com.example.radiokazansings

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    private lateinit var player: ExoPlayer
    var audioUrl = "https://av.bimradio.ru/bim_mp3_128k"

    companion object {
        fun startService(context: Context, message: String, isPlaying: Boolean) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("message", message)
            startIntent.putExtra("isPlaying", isPlaying)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }


    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(audioUrl)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("message")
        val isPlaying = intent?.getBooleanExtra("isPlaying", false) == true
        player.playWhenReady = isPlaying
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        pendingIntent

        val remoteView = RemoteViews(packageName, R.layout.custom_navigation_player)
        remoteView.setTextViewText(R.id.tv_status, "Is playing")
        remoteView.setImageViewResource(R.id.iv_logo_statusbar, R.drawable.im_logo_statusbar)
        remoteView.setImageViewResource(R.id.imb_play,
            if (isPlaying) {
                R.drawable.image_pause
            } else {
                R.drawable.image_play
            }
        )
        remoteView.setImageViewResource(R.id.imb_like, R.drawable.like)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Казань поет")
            .setContentText("Онлайн-радио")
            .setSmallIcon(R.drawable.small_icon_notification)
            .setCustomBigContentView(remoteView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setContentText(input)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}

