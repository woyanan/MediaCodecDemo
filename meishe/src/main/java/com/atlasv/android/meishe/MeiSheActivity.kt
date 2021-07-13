package com.atlasv.android.meishe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.meishe.bean.ClipInfo
import com.atlasv.android.meishe.bean.TimelineData
import com.atlasv.android.meishe.utils.CommonUtil
import com.atlasv.android.meishe.utils.TimelineUtil
import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsTimeline
import kotlinx.android.synthetic.main.activity_meishe.*

class MeiSheActivity : AppCompatActivity() {
    companion object {
        const val PATH = "path"

        fun start(context: Context, path: String) {
            val intent = Intent(context, MeiSheActivity::class.java)
            intent.putExtra(PATH, path)
            context.startActivity(intent)
        }
    }

    /**
     * 时间线，编辑场景的时间轴实体
     * Timeline. Edit the timeline entity of the scene
     */
    private var timeline: NvsTimeline? = null
    private var streamingContext: NvsStreamingContext? = null

    enum class Enum {
        CONCAT
    }

    private var value = Enum.CONCAT
    private val pickVideoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                val path = CommonUtil.contentUri2FilePath(this, uri)
                if (!path.isNullOrEmpty()) {
                    when (value) {
                        Enum.CONCAT -> {
                            concat(path)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStreamingContext()
        setContentView(R.layout.activity_meishe)
        initTimeline()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        connectTimelineWithLiveWindow()
    }

    private fun setOnClickListener() {
        ivPlay?.setOnClickListener {
            when (streamingContext?.streamingEngineState) {
                NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK -> {
                    if (streamingContext?.isPlaybackPaused == true) {
                        ivPlay.setImageResource(R.mipmap.ic_pause)
                        resume()
                    } else {
                        ivPlay.setImageResource(R.mipmap.ic_play)
                        pause()
                    }

                }
                else -> {
                    val durationUs = timeline?.duration ?: 0
                    play(0, durationUs)
                }
            }
        }
        playSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val durationUs = timeline?.duration ?: 0
                    val timeStamp = progress * durationUs / 100
                    seekTo(timeStamp, 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        btnConcat?.setOnClickListener {
            value = Enum.CONCAT
            pickVideo()
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
        val filePath = intent?.getStringExtra(PATH)
        val clipInfo = ClipInfo()
        clipInfo.filePath = filePath
        val pathList = arrayListOf(clipInfo)
        TimelineData.instance.clipInfoList.addAll(pathList)
        timeline = TimelineUtil.createTimeline()
    }

    private fun connectTimelineWithLiveWindow() {
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
                when (state) {
                    NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK -> {
                        ivPlay.setImageResource(R.mipmap.ic_pause)
                    }
                    else -> {
                        ivPlay.setImageResource(R.mipmap.ic_play)
                    }
                }
            }

            override fun onFirstVideoFramePresented(p0: NvsTimeline?) {

            }

        })
        streamingContext?.connectTimelineWithLiveWindowExt(timeline, liveWindow)
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

    private fun seekTo(timestamp: Long, seekShowMode: Int) {
        streamingContext?.seekTimeline(
            timeline,
            timestamp,
            NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
            seekShowMode
        )
    }

    private fun pause() {
        streamingContext?.pausePlayback()
    }

    private fun resume() {
        streamingContext?.resumePlayback()
    }

    private fun concat(path: String) {
        val clipInfo = ClipInfo()
        clipInfo.filePath = path
        TimelineData.instance.clipInfoList.add(clipInfo)
        TimelineUtil.reBuildVideoTrack(timeline)
        refreshLiveWindowFrame()
    }

    private fun refreshLiveWindowFrame() {
        if (streamingContext?.streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            val timestamp = streamingContext?.getTimelineCurrentPosition(timeline) ?: return
            seekTo(timestamp, 0)
        }
    }

    private fun pickVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickVideoLauncher.launch(intent)
    }
}