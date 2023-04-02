package com.example.music_play

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_play.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA:ArrayList<music>
        var songPosition:Int = 0
        var isPlaying:Boolean = false
        var musicService:MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // for starting service
        val intenta = Intent(this,MusicService::class.java)
        bindService(intenta,this, BIND_AUTO_CREATE)
        startService(intenta)

        initializeLayout()
        binding.playpauseBtnPA.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }

        binding.prevBtnPA.setOnClickListener(){prevNextSong(increment = false)}

        binding.nextBtnPA.setOnClickListener(){prevNextSong(increment = true)}

        binding.seekbarPA.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) =Unit


            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }
    private fun setlayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.mipmap.ic_launcher).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
    }

    private fun createMediaPlayer(){
        try{
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playpauseBtnPA.setIconResource(R.drawable.pause_icon)
            musicService!!.shownotification(R.drawable.pause_icon)
            binding.tvseekbarstart.text = formatduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvseekbarend.text = formatduration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress = 0
            binding.seekbarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        }catch (e:Exception){return}
    }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setlayout()
            }

            "MainActivity"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setlayout()
            }
        }
    }

    private fun playMusic(){
        binding.playpauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.shownotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playpauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.shownotification(R.drawable.play_icon)
        isPlaying =  false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment:Boolean){
        if (increment){
            setSongPosition(increment = true)
            setlayout()
            createMediaPlayer()
        }else{
            setSongPosition(increment = false)
            setlayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as  MusicService.Mybinder
        musicService = binder.currentservice()
        createMediaPlayer()
        musicService!!.seekbarsetup()
//        musicService!!.mediaPlayer!!.setOnCompletionListener { this }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try{setlayout()}catch (e:Exception){return}
    }
}