package com.atlasv.android.mediacodec.surfacetexture

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.mediacodecdemo.R
import kotlinx.android.synthetic.main.activity_video2.*

/**
 * Created by woyanan on 2021/7/6
 */
class VideoActivity2 : AppCompatActivity() {
    companion object {
        const val videoPath = "/storage/emulated/0/DCIM/Camera/ff00c75b7b424a7291ebb54780703a89.mp4"

        fun start(context: Context) = Intent(context, VideoActivity2::class.java)
    }

    private val mediaPlayer by lazy { MediaPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        setupMediaPlayer()

        pause?.setOnClickListener {
            mediaPlayer.pause()
        }
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val timeMs = 19319 * p1 / 100
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mediaPlayer.seekTo(timeMs.toLong(), MediaPlayer.SEEK_CLOSEST)
                } else {
                    mediaPlayer.seekTo(timeMs)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    /**
     * MediaPlayer
     */
    private fun setupMediaPlayer() {
        surfaceView?.render?.onSurfaceChanged = {
            mediaPlayer.setOnPreparedListener { mediaPlayer -> mediaPlayer.start() }
            val surface = Surface(surfaceView?.render?.videoTexture)
            mediaPlayer.setSurface(surface)
            surface.release()
            kotlin.runCatching {
                mediaPlayer.setDataSource(videoPath)
                mediaPlayer.prepareAsync()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}