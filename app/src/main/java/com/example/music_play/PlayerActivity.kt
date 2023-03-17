package com.example.music_play

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_play.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object{
        lateinit var musicListPA:ArrayList<music>
        var songPosition:Int = 0
        var mediaPlayer:MediaPlayer ?= null
        var isPlaying:Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_player)
        initializeLayout()
        binding.playpauseBtnPA.setOnClickListener{
            if(isPlaying) pauseMusic()
            else playMusic()
        }

        binding.prevBtnPA.setOnClickListener(){prevNextSong(increment = false)}

        binding.nextBtnPA.setOnClickListener(){prevNextSong(increment = true)}
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
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.playpauseBtnPA.setIconResource(R.drawable.pause_icon)
        }catch (e:Exception){return}
    }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setlayout()
                createMediaPlayer()
            }
        }
    }

    private fun playMusic(){
        binding.playpauseBtnPA.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic(){
        binding.playpauseBtnPA.setIconResource(R.drawable.play_icon)
        isPlaying =  false
        mediaPlayer!!.pause()
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

    private fun setSongPosition(increment: Boolean){
        if (increment){
            if(musicListPA.size-1 == songPosition)
                songPosition = 0
            else
                ++songPosition
        }
        else{
            if( songPosition == 0)
                songPosition = musicListPA.size-1
            else
                --songPosition
        }
    }
}