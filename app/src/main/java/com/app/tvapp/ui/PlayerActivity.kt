package com.app.tvapp.ui

import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Rational
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.tvapp.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var url: String? = null

    private lateinit var listener: Player.EventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        url = intent.getStringExtra("channel")

        listener = object: Player.EventListener{
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when(state){
                    ExoPlayer.STATE_READY -> binding.progress.isVisible = false
                    Player.STATE_IDLE, Player.STATE_BUFFERING-> binding.progress.isVisible = true
                }
            }
        }

    }

    private fun initPlayer(){
        player = SimpleExoPlayer.Builder(this).build()
        url?.let{
            player?.addMediaItem(
                MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(it).build()
            )
        }
        player?.prepare()
        player?.addListener(listener)
        binding.player.player = player
        player?.playWhenReady = true
    }

    private fun releasePlayer(){
        player?.removeListener(listener)
        player?.release()
        player = null
    }

    private fun startPIP(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(20, 9))
//                    .setActions(listOf(
//                        RemoteAction()
//                    ))
                    .build()
            )
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            enterPictureInPictureMode()
        }
    }

    override fun onBackPressed() {
        startPIP()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24 || player == null)) {
            initPlayer();
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer();
        }
    }
}