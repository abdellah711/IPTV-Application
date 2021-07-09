package com.app.tvapp.ui

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.app.tvapp.R
import com.app.tvapp.databinding.ActivityPlayerBinding
import com.app.tvapp.viewmodels.PlayerViewModel
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true


    private val viewModel: PlayerViewModel by viewModels()

    private lateinit var listener: Player.EventListener

    private val errorDialog: AlertDialog by lazy {

        AlertDialog.Builder(this@PlayerActivity, R.style.ErrorDialog)
            .setTitle(R.string.dialog_error_title)
            .setMessage(R.string.dialog_error_msg)
            .setPositiveButton(R.string.go_back) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.retry) { _, _ ->
                player?.prepare()
            }
            .create()
    }

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

        updateChannel(intent)

        setupPlayerChannelDetails()
        setupPlayerListeners()

    }

    private fun setupPlayerChannelDetails() {
        binding.player.apply {
            findViewById<TextView>(R.id.title_tv).text = viewModel.channel?.name ?: ""
            findViewById<TextView>(R.id.categ).text = viewModel.channel?.category ?: ""

            Glide.with(this@PlayerActivity)
                .load(viewModel.channel?.logo)
                .error(R.drawable.ic_no_logo)
                .into(findViewById(R.id.channel_icon))

        }
    }

    private fun setupToolbar() {
        val toolbar = binding.player.findViewById<Toolbar>(R.id.toolbar)
        toolbar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupPlayerListeners() {
        listener = object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)

                when (state) {
                    ExoPlayer.STATE_READY -> {
                        binding.progress.isVisible = false
                        binding.player.keepScreenOn = true
                        errorDialog.dismiss()
                    }
                    Player.STATE_IDLE, Player.STATE_BUFFERING -> {
                        binding.progress.isVisible = true
                        binding.player.keepScreenOn = false
                    }
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                errorDialog.show()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updateChannel(intent)
        viewModel.channel?.let {
            releasePlayer()
            initPlayer()
            setupPlayerChannelDetails()
        }
    }

    private fun updateChannel(intent: Intent?) {
        viewModel.channel = intent?.getParcelableExtra("channel")
        invalidateOptionsMenu()
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        viewModel.channel?.url?.let {
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
                    .setAspectRatio(Rational(19, 9))
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
        favItem?.isChecked = viewModel.isFav
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
                viewModel.saveFav(!item.isChecked)
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (player?.isPlaying == true) {
            startPIP()
            return
        }
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
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