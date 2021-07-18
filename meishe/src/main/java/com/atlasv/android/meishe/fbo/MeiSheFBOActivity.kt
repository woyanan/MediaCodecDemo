package com.atlasv.android.meishe.fbo

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.meishe.MeiSheActivity
import com.atlasv.android.meishe.R
import com.atlasv.android.meishe.bean.ClipInfo
import com.atlasv.android.meishe.bean.TimelineData
import com.atlasv.android.meishe.utils.CommonUtil
import com.atlasv.android.meishe.utils.TimelineUtil
import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsTimeline
import kotlinx.android.synthetic.main.activity_meishe.*
import kotlinx.android.synthetic.main.activity_opengl_player.*
import kotlinx.android.synthetic.main.activity_opengl_player.ivPlay

/**
 * Created by woyanan on 2021/7/18
 */
class MeiSheFBOActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context, path: String) {
            val intent = Intent(context, MeiSheFBOActivity::class.java)
            intent.putExtra(CommonUtil.PATH, path)
            context.startActivity(intent)
        }
    }

    private lateinit var drawer: IDrawer

    /**
     * 时间线，编辑场景的时间轴实体
     * Timeline. Edit the timeline entity of the scene
     */
    private var timeline: NvsTimeline? = null
    private var streamingContext: NvsStreamingContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initStreamingContext()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_player)
        initTimeline()
        initRender()
        ivPlay?.setOnClickListener {
            val durationUs = timeline?.duration ?: 0
            play(0, durationUs)
        }
    }

    private fun initStreamingContext() {
        streamingContext = NvsStreamingContext.getInstance()
        if (streamingContext == null) {
            val licensePath = "assets:/meishesdk.lic"
            streamingContext = NvsStreamingContext.init(
                applicationContext,
                licensePath,
                NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT
            )
        }
    }

    private fun initTimeline() {
        val filePath = intent?.getStringExtra(MeiSheActivity.PATH)
        val clipInfo = ClipInfo()
        clipInfo.filePath = filePath
        val pathList = arrayListOf(clipInfo)
        TimelineData.instance.clipInfoList.addAll(pathList)
        timeline = TimelineUtil.createTimeline()
    }

    private fun initRender() {
        drawer = SoulVideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            connectTimelineWithLiveWindow(it)
        }
        gl_surface.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        gl_surface.setRenderer(render)
    }

    private fun connectTimelineWithLiveWindow(st: SurfaceTexture) {
        streamingContext?.setPlaybackCallback(object : NvsStreamingContext.PlaybackCallback {
            override fun onPlaybackPreloadingCompletion(nvsTimeline: NvsTimeline?) {
            }

            override fun onPlaybackStopped(nvsTimeline: NvsTimeline?) {
            }

            override fun onPlaybackEOF(nvsTimeline: NvsTimeline?) {
            }
        })
        streamingContext?.setPlaybackCallback2 { nvsTimeline, curPositionUs ->
            val durationUs = timeline?.duration ?: 0
            if (durationUs > 0) {
                val progress = ((curPositionUs * 100f / durationUs)).toInt()
                playSeekBar.progress = progress
            }
        }

        streamingContext?.setStreamingEngineCallback(object :
            NvsStreamingContext.StreamingEngineCallback {
            override fun onStreamingEngineStateChanged(state: Int) {
            }

            override fun onFirstVideoFramePresented(p0: NvsTimeline?) {

            }

        })
        this.runOnUiThread {
            streamingContext?.connectTimelineWithSurfaceTexture(
                timeline,
                st
            )
        }
    }

    private fun play(startTime: Long, endTime: Long) {
        streamingContext?.playbackTimeline(
            timeline,
            startTime,
            endTime,
            NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
            true,
            0
        )
    }
}