package com.app.tvapp.ui

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.app.tvapp.R
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

class PlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var channel: DBChannel? = null
    private var playWhenReady = true

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

        setupToolbar()

        channel = intent.getParcelableExtra("channel")
        setupPlayerChannelDetails()
        setupPlayerListener()

    }

    private fun setupPlayerChannelDetails() {
        binding.player.apply {
            findViewById<TextView>(R.id.title_tv).text = channel?.name ?: ""
            findViewById<TextView>(R.id.categ).text = channel?.category ?: ""

            Glide.with(this@PlayerActivity)
                .load(channel?.logo)
                .into(findViewById(R.id.channel_icon))

        }
    }

    private fun setupToolbar() {
        val toolbar = binding.player.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupPlayerListener() {
        listener = object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)

                when (state) {
                    ExoPlayer.STATE_READY -> {
                        binding.progress.isVisible = false
                        binding.player.keepScreenOn = true
                    }
                    Player.STATE_IDLE, Player.STATE_BUFFERING -> {
                        binding.progress.isVisible = true
                        binding.player.keepScreenOn = false
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        channel = intent?.getParcelableExtra("channel")
        channel?.let {
            releasePlayer()
            initPlayer()
            setupPlayerChannelDetails()
        }

        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        channel?.url?.let {
            player?.addMediaItem(
                MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(it).build()
            )
        }
        player?.prepare()
        player?.addListener(listener)
        binding.player.player = player
        player?.playWhenReady = playWhenReady
    }

    private fun releasePlayer() {
        player?.removeListener(listener)
        playWhenReady = player?.playWhenReady ?: true
        player?.release()
        player = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun startPIP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(20, 9))
                    .build()
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            enterPictureInPictureMode()
        }
        binding.player.hideController()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        if (isInPictureInPictureMode) {
            binding.player.hideController()
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.player_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val favItem = menu?.findItem(R.id.menu_fav)
        favItem?.icon =
            if (favItem?.isChecked == true) ContextCompat.getDrawable(this, R.drawable.ic_star_rate)
            else ContextCompat.getDrawable(this, R.drawable.ic_star_outline)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_pip -> {
                startPIP()
                true
            }
            R.id.menu_fav -> {
                item.isChecked = !item.isChecked
                invalidateOptionsMenu()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (player?.isPlaying == true){
            startPIP()
            return
        }
        super.onBackPressed()
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
            initPlayer()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }
}