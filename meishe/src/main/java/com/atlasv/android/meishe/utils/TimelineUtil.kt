package com.atlasv.android.meishe.utils

import com.atlasv.android.meishe.bean.TimelineData
import com.meicam.sdk.*

/**
 * Created by woyanan on 2021/7/13
 */
object TimelineUtil {
    private const val VIDEO_VOLUME_DEFAULT_VALUE = 1.0f

    fun createTimeline(): NvsTimeline? {
        val timeline = newTimeline(TimelineData.instance.videoResolution)
        if (!buildVideoTrack(timeline)) {
            return timeline
        }
        timeline?.appendAudioTrack()
        return timeline
    }

    private fun buildVideoTrack(timeline: NvsTimeline?): Boolean {
        val videoTrack = timeline?.appendVideoTrack() ?: return false
        val clipInfoList = TimelineData.instance.clipInfoList
        for (i in clipInfoList.indices) {
            val clipInfo = clipInfoList[i]
            videoTrack.appendClip(clipInfo.filePath)
        }
        videoTrack.setVolumeGain(VIDEO_VOLUME_DEFAULT_VALUE, VIDEO_VOLUME_DEFAULT_VALUE)
        return true
    }

    private fun newTimeline(videoResolution: NvsVideoResolution?): NvsTimeline? {
        val context = NvsStreamingContext.getInstance() ?: return null

        videoResolution?.imagePAR = NvsRational(1, 1)
        val videoFps = NvsRational(30, 1)

        val audioEditRes = NvsAudioResolution()
        audioEditRes.sampleRate = 44100
        audioEditRes.channelCount = 2

        return context.createTimeline(videoResolution, videoFps, audioEditRes)
    }

}