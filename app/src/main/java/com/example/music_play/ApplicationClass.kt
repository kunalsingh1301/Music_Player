package com.example.music_play

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class ApplicationClass: Application() {
    companion object{
        const val Channel_Id= "Channel_1"
        const val play= "Play"
        const val next= "Next"
        const val previous= "Prev"
        const val exit = "Exit"
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {val notificationChannel = NotificationChannel(Channel_Id, "Now Playing song", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "Showing song"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}