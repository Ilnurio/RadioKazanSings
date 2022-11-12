package com.example.radiokazansings

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat


private const val UPDATE_PLAYING_STATE = "UpdatePlayingStateTo"
const val ACTION_UPDATE_PLAYER_BUTTONS = "UpdatePlayerButton"

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundServiceKotlin"

    private var picture: Bitmap? = null

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("message", message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag", "ResourceAsColor")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ForegroundService::", "onStartCommand")
        val input = intent?.getStringExtra("message")
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        if (intent?.hasExtra(UPDATE_PLAYING_STATE) == true) {
            sendBroadcast(
                Intent(ACTION_UPDATE_PLAYER_BUTTONS).putExtra("isPlaying", isRadioPlaying())
            )
            getRadioPlayer().playWhenReady = !isRadioPlaying()
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playServiceIntent = Intent(this, ForegroundService::class.java)
        playServiceIntent.putExtra(UPDATE_PLAYING_STATE, !isRadioPlaying())

        val playServicePendingUpdateIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                this, 1, playServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(
                this, 1, playServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val dismissIn = Intent("dismiss")
        dismissIn.addCategory("dismiss_category")

        val dismissIntent = PendingIntent.getBroadcast(
            this, 1,
            dismissIn, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        picture = if (resources != null) BitmapFactory.decodeResource(
            resources,
            R.drawable.im_logo_statusbar_notif
        ) else null
        val mediaSession = MediaSessionCompat(this, "tag")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.small_icon_notification)
            .setPriority(PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setContentText(input)
            .setContentTitle("Онлайн-радио")
            .setContentText("Казань поет")
            .setOngoing(isRadioPlaying())
            //.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setLargeIcon(picture)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(
                if (isRadioPlaying()) {
                    R.drawable.image_pause
                } else {
                    R.drawable.image_play
                }, "Play",
                playServicePendingUpdateIntent
            )
            .setDeleteIntent(dismissIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val Channel_1 = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            Channel_1.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(Channel_1)
        }
    }
}

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "dismiss") {
            (context as ForegroundService).stopSelf()
        }
    }

}