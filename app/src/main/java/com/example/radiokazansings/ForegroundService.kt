package com.example.radiokazansings

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundServiceKotlin"
    private lateinit var player: ExoPlayer
    var audioUrl = "https://av.bimradio.ru/bim_mp3_128k"
    private lateinit var picture: Bitmap

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
        notificationIntent.putExtra("isPlaying", isPlaying)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val playServiceIntent = Intent(this, ForegroundService::class.java)
        playServiceIntent.putExtra("isPlaying", !isPlaying)

        sendBroadcast(
            Intent("UpdatePlayerButton")
                .putExtra("isPlaying", isPlaying)
        )

        val playServicePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 1, playServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getService(this, 1, playServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        picture = BitmapFactory.decodeResource(resources,R.drawable.im_logo_statusbar)
        val mediaSession = MediaSessionCompat(this, "tag")
        val controller = mediaSession.controller

        /*val remoteView = RemoteViews(packageName, R.layout.custom_navigation_player)
        remoteView.setTextViewText(R.id.tv_status, "Is playing")
        remoteView.setImageViewResource(R.id.iv_logo_statusbar, R.drawable.im_logo_statusbar)
        remoteView.setImageViewResource(R.id.imb_play,
            if (isPlaying) {
                R.drawable.image_pause
            } else {
                R.drawable.image_play
            }
        )
        remoteView.setImageViewResource(R.id.imb_like, R.drawable.like)*/

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.small_icon_notification)
            .setPriority(PRIORITY_HIGH)
           // .setContentIntent(controller.sessionActivity)
            .setContentIntent(pendingIntent)
            //.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_STOP))
            .setContentText(input)
            .setOngoing(isPlaying)
            .setContentTitle("Онлайн-радио")
            .setContentText("Казань поет")
            .setLargeIcon(picture)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mediaSession.getSessionToken()))
            .addAction(if (isPlaying) {
                R.drawable.image_pause
            } else {
                R.drawable.image_play
            }
                , "Play",
                playServicePendingIntent) // #0
            //.addAction(R.drawable.image_pause, "Pause", pendingIntent) // #1
            /*.addAction(NotificationCompat.Action(
                if (isPlaying) {
                    R.drawable.image_pause
                } else {
                    R.drawable.image_play
                },
                getString(R.string.Pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )*/
            .build()

  /*      if (isPlaying) {
            notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        } else {

        }*/


        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val Channel_1 = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH)
            Channel_1.setSound(null,null)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(Channel_1)
        }
    }
}

