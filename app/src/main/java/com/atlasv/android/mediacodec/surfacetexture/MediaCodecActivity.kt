package com.atlasv.android.mediacodec.surfacetexture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.mediacodec.R
import com.atlasv.android.mediacodec.surfacetexture.core.DecodeThread
import com.atlasv.android.mediacodec.surfacetexture.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_video.*

/**
 * Created by woyanan on 2021/7/8
 */
class MediaCodecActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context, path: String) {
            val intent = Intent(context, MediaCodecActivity::class.java)
            intent.putExtra(CommonUtil.PATH, path)
            context.startActivity(intent)
        }
    }

    private val decodeThread by lazy { DecodeThread() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        setupMediaCodec()
        play?.setOnClickListener {
            decodeThread.pause()
            play?.setText(if (decodeThread.isPlaying()) R.string.pause else R.string.play)
        }
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    decodeThread.seekTo(p1)
                }
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

    private fun setupMediaCodec() {
        surfaceView?.render?.onSurfaceChanged = {
            val surface = Surface(surfaceView?.render?.videoTexture)
            val path = intent?.getStringExtra(CommonUtil.PATH)
            if (!path.isNullOrEmpty()) {
                if (decodeThread.init(surface, path)) {
                    decodeThread.start()
                }
                surface.release()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        decodeThread.pause()
    }

}