package com.example.music_play

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService: Service() {
    private var mybinder = Mybinder()
    var mediaPlayer: MediaPlayer? = null

    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext,"My_music")
        return mybinder
    }
    inner class Mybinder:Binder(){
        fun currentservice():MusicService{
            return this@MusicService
        }
    }

    fun shownotification(playpauseBtn:Int){

        val previntent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.previous)
        val previntentpending = PendingIntent.getBroadcast(baseContext,0,previntent,PendingIntent.FLAG_IMMUTABLE)

        val playintent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.play)
        val playintentpending = PendingIntent.getBroadcast(baseContext,0,playintent,PendingIntent.FLAG_IMMUTABLE)

        val nextintent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.next)
        val nextintentpending = PendingIntent.getBroadcast(baseContext,0,nextintent,PendingIntent.FLAG_IMMUTABLE)

        val exitintent = Intent(baseContext,NotificationReciever::class.java).setAction(ApplicationClass.exit)
        val exitintentpending = PendingIntent.getBroadcast(baseContext,0,exitintent,PendingIntent.FLAG_IMMUTABLE)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val path = if (imgArt != null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources,R.drawable.splash_screen)
        }

        var notification = NotificationCompat.Builder(this, ApplicationClass.Channel_Id)
            .setSmallIcon(R.drawable.splash_screen)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.playlist_icon)
            .setLargeIcon(path)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previous_Icon",previntentpending)
            .addAction(playpauseBtn, "Play_Icon",playintentpending)
            .addAction(R.drawable.next_icon, "Next_Icon",nextintentpending)
            .addAction(R.drawable.exit_to_app_icon, "Exit_Icon",exitintentpending)
            .build()
        startForeground(13,notification)
    }

    fun createMediaPlayer(){
        try{
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.musicService!!.mediaPlayer!!.start()
            PlayerActivity.isPlaying = true
            PlayerActivity.binding.playpauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.shownotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvseekbarstart.text = formatduration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvseekbarend.text = formatduration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbarPA.progress = 0
            PlayerActivity.binding.seekbarPA.max = mediaPlayer!!.duration
        }catch (e:Exception){return}
    }

    fun seekbarsetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvseekbarstart.text = formatduration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
}