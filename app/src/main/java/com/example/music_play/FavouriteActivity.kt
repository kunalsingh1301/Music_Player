package com.example.music_play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.music_play.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_favourite)
    }
}