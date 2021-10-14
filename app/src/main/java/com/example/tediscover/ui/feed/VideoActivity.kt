package com.example.tediscover.ui.feed

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tediscover.R
import com.example.tediscover.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.layout_exoplayer_control_views.view.*


class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private var videoUri: Uri? = null

    private var playerControlView: PlayerView? = null
    private var fullscreenButton: ImageView? = null
    private var player: SimpleExoPlayer? = null
    private var fullScreen = false
    private var fullScreenBtnFlag = 0

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val urlString = intent?.extras?.getString("LINK").toString()
        videoUri = Uri.parse(urlString)

        playerControlView = binding.videoView
        fullscreenButton = playerControlView!!.exo_fullscreen_icon
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        fullscreenButton!!.setOnClickListener {
            fullScreenBtnFlag = 1
            requestedOrientation = if (fullScreen) {
                showSystemUI()
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                hideSystemUI()
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        fullScreen = if (fullScreen || fullScreenBtnFlag != 1) {
            fullscreenButton!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_close)
            )
            hideSystemUI()
            false
        } else {
            fullscreenButton!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_open)
            )
            showSystemUI()
            true
        }
    }


    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer(videoUri)
        }
    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }


    override fun setRequestedOrientation(requestedOrientation: Int) {
        super.setRequestedOrientation(requestedOrientation)
        fullScreen = requestedOrientation != 1
        val params = binding.videoView.layoutParams as RelativeLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.videoView.layoutParams = params
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
    }


    private fun initializePlayer(videoUri: Uri?) {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        // Create a HLS media source pointing to a playlist uri.
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(videoUri!!))

        player = SimpleExoPlayer.Builder(this)
                .build()
                .also {
                    binding.videoView.player = it

                    it.setMediaSource(hlsMediaSource)
                    it.playWhenReady = playWhenReady
                    it.seekTo(currentWindow, playbackPosition)
                    it.addListener(playbackStateListener)
                    it.prepare()
                }
    }


    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.removeListener(playbackStateListener)
            it.release()
        }
        player = null
    }


    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }


    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
}


private fun playbackStateListener() = object : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
        val stateString: String = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
    }
}