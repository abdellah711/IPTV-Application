package com.app.tvapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.tvapp.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        player = SimpleExoPlayer.Builder(this).build()
        intent.getStringExtra("channel")?.let {
            player.addMediaItem(
                MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(it).build()
            )
        }

        binding.player.player = player
        player.play()
    }
}