package com.atlasv.android.mediacodecdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import tech.thdev.mediacodecexample.video.MediaCodecDecoder
import tech.thdev.mediacodecexample.video.MediaDecodeThread
import tech.thdev.mediacodecexample.video.MediaRenderThread


class VideoActivity : AppCompatActivity(), SurfaceHolder.Callback {

    companion object {

        fun start(context: Context): Intent =
            Intent(context, VideoActivity::class.java)
    }

    //    private var videoDecode: VideoDecodeThread? = null
    private var mediaCodecDecode: MediaCodecDecoder? = null
    private val mediaDecodeThread by lazy {
        MediaDecodeThread(mediaCodecDecode)
    }
    private val mediaRenderThread by lazy {
        MediaRenderThread(mediaCodecDecode)
    }

    private var surfaceView: SurfaceView? = null
    private var seekbar: SeekBar? = null
    private var play: Button? = null
    private var pause: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_decode)
        surfaceView = findViewById(R.id.surfaceView)
        seekbar = findViewById(R.id.seekbar)
        pause = findViewById(R.id.pause)
        surfaceView?.holder?.addCallback(this@VideoActivity)
//        videoDecode = VideoDecodeThread()
        mediaCodecDecode = MediaCodecDecoder()
        pause?.setOnClickListener {
//            videoDecode?.pause()
            mediaDecodeThread.pause()
            mediaRenderThread.pause()
        }
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                videoDecode?.seekTo(p1)
                mediaDecodeThread.seekTo(p1)
                mediaRenderThread.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mediaCodecDecode?.init(holder.surface) == true) {
//            videoDecode?.start()
            mediaDecodeThread.start()
            mediaRenderThread.start()
        } else {
//            videoDecode = null
            mediaCodecDecode = null
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        videoDecode?.close()
        mediaDecodeThread.close()
        mediaRenderThread.close()
    }

    override fun onPause() {
        super.onPause()
//        videoDecode?.close()
        mediaDecodeThread.pause()
        mediaRenderThread.pause()
    }
}