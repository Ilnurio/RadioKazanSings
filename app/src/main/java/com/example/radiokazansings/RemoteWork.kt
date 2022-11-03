package com.example.radiokazansings

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class RemoteWork: Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

     override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                Thread.sleep(5000)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        this.startForegroundService(Intent(this, ForegroundService::class.java))
                    }
                    else -> {
                        this.startService(Intent(this, ForegroundService::class.java))
                    }
                }
            }
            else -> {
                // do nothing
            }
        }
         return
    }
}