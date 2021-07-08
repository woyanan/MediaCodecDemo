package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.mediacodecdemo.R
import com.atlasv.android.mediacodecdemo.mediacodec.MediaCodecDecoder
import com.atlasv.android.mediacodecdemo.mediacodec.MediaDecodeThread
import com.atlasv.android.mediacodecdemo.mediacodec.MediaRenderThread
import kotlinx.android.synthetic.main.activity_video2.*

/**
 * Created by woyanan on 2021/7/8
 */
class VideoActivity3 : AppCompatActivity() {
    companion object {
        fun start(context: Context) = Intent(context, VideoActivity3::class.java)
    }

    private var mediaCodecDecode: MediaCodecDecoder? = null
    private val mediaDecodeThread by lazy {
        MediaDecodeThread(mediaCodecDecode)
    }
    private val mediaRenderThread by lazy {
        MediaRenderThread(mediaCodecDecode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        setupMediaCodec()
        pause?.setOnClickListener {
            mediaDecodeThread.pause()
            mediaRenderThread.pause()
        }
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mediaDecodeThread.seekTo(p1)
                mediaRenderThread.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    /**
     * MediaCodec
     */
    private fun setupMediaCodec() {
        mediaCodecDecode = MediaCodecDecoder()
        surfaceView?.render?.onSurfaceChanged = {
            val surface = Surface(surfaceView?.render?.videoTexture)
            if (mediaCodecDecode?.init(surface) == true) {
                mediaDecodeThread.start()
                mediaRenderThread.start()
            } else {
                mediaCodecDecode = null
            }
            surface.release()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaDecodeThread.pause()
        mediaRenderThread.pause()
    }

}