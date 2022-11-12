package com.example.radiokazansings

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


private const val UPDATE_PLAYING_STATE = "UpdatePlayingStateTo"
const val ACTION_UPDATE_PLAYER_BUTTONS = "UpdatePlayerButton"
private const val PLAYER_NOTIFICATION_ID = 1
private const val KEY_IS_PLAYING = "isPlaying"
private const val CHANNEL_ID = "ForegroundServiceKotlin"
private const val ACTION_DISMISS = "action_dismiss"

class ForegroundService : Service() {

    private val dismissBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DISMISS) {
                stopSelf()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        getRadioPlayer().attach()
        registerReceiver(dismissBroadcastReceiver, IntentFilter(ACTION_DISMISS))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dismissBroadcastReceiver)
        getRadioPlayer().detach()
    }

    @SuppressLint("RemoteViewLayout", "UnspecifiedImmutableFlag", "ResourceAsColor")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ForegroundService::", "onStartCommand")
        val input = intent?.getStringExtra("message")
        createNotificationChannel()

        if (intent?.hasExtra(UPDATE_PLAYING_STATE) == true) {
            sendBroadcast(
                Intent(ACTION_UPDATE_PLAYER_BUTTONS).putExtra(KEY_IS_PLAYING, isRadioPlaying())
            )
            getRadioPlayer().playWhenReady = !isRadioPlaying()
        }
        val notification = createNotification(input)

        if (isRadioPlaying()) {
            startForeground(PLAYER_NOTIFICATION_ID, notification)
        } else {
            NotificationManagerCompat.from(this).notify(PLAYER_NOTIFICATION_ID, notification)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_DETACH)
            } else {
                stopForeground(false)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(input: String?): Notification {

        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            Intent(ACTION_DISMISS),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainActivityPendingIntent = PendingIntent.getActivity(
            this, 0, mainActivityIntent,
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
        val picture = if (resources != null) BitmapFactory.decodeResource(
            resources,
            R.drawable.im_logo_statusbar_notif
        ) else null

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.small_icon_notification)
            .setPriority(PRIORITY_HIGH)
            .setContentIntent(mainActivityPendingIntent)
            .setContentText(input)
            .setContentTitle("Онлайн-радио")
            .setContentText("Казань поет")
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setLargeIcon(picture)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )
            .addAction(
                if (isRadioPlaying()) {
                    R.drawable.image_pause_notif
                } else {
                    R.drawable.image_play_notif
                }, "Play",
                playServicePendingUpdateIntent
            )
            .setDeleteIntent(dismissPendingIntent)
            .build()
        return notification
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Kazan radio notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            getSystemService(NotificationManager::class.java).apply {
                createNotificationChannel(channel)
            }
        }
    }

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("message", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
    }
}