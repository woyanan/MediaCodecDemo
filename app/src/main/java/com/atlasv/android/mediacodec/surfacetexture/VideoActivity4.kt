package com.atlasv.android.mediacodec.surfacetexture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.mediacodecdemo.R
import com.atlasv.android.mediacodec.surfacetexture.core.DecodeThread
import kotlinx.android.synthetic.main.activity_video2.*

/**
 * Created by woyanan on 2021/7/8
 */
class VideoActivity4 : AppCompatActivity() {
    companion object {
        fun start(context: Context) = Intent(context, VideoActivity4::class.java)
    }

    private val decodeThread by lazy {
        DecodeThread()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        setupMediaCodec()
        pause?.setOnClickListener {
            decodeThread.pause()
        }
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                decodeThread.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        decodeThread.onNotifyChange = {
            seekbar?.progress = it
        }
    }

    /**
     * MediaCodec
     */
    private fun setupMediaCodec() {
        surfaceView?.render?.onSurfaceChanged = {
            val surface = Surface(surfaceView?.render?.videoTexture)
            if (decodeThread.init(surface)) {
                decodeThread.start()
            }
            surface.release()
        }
    }

    override fun onPause() {
        super.onPause()
        decodeThread.pause()
    }

}