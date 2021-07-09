package com.atlasv.android.mediacodec.mediacodec

import android.util.Log

/**
 * Created by woyanan on 2021/7/1
 */
class MediaRenderThread(private val mediaCodecDecode: MediaCodecDecoder?) : Thread() {
    companion object {
        private const val TAG = "MediaRenderThread"
    }

    private var isStop = false
    private var isPause = false

    //Seek
    private var isSeeking = false
    private var seekTimeUs = 0L

    override fun run() {
        var isFirst = true
        var startWhen = 0L

        while (true) {
            if (isStop) {
                break
            }
            if (isPause && isSeeking.not()) {
                continue
            }
            val presentationTimeUs = mediaCodecDecode?.getPresentationTimeUs() ?: -1

            if (isSeeking) {
                // seek时按顺序解码, 但是跳帧显示, 100ms显示一帧
                if (presentationTimeUs < seekTimeUs) {
                    println("---------------->dequeueOutputBuffer false")
                    mediaCodecDecode?.dequeueOutputBuffer(true)
                    continue
                } else {
                    println("---------------->dequeueOutputBuffer true")

                }
            }

            mediaCodecDecode?.dequeueOutputBuffer(true)
            isSeeking = false

            if (presentationTimeUs > 0) {
                if (isFirst) {
                    startWhen = System.currentTimeMillis()
                    isFirst = false
                }
            }

            kotlin.runCatching {
                val timelineMs = System.currentTimeMillis() - startWhen
                val sleepTime = presentationTimeUs / 1000 - timelineMs
                Log.d(
                    TAG,
                    "info.presentationTimeUs: " + (presentationTimeUs / 1000).toString() + " playTime: " + (System.currentTimeMillis() - startWhen).toString() + " sleepTime : " + sleepTime
                )
                if (sleepTime > 0) {
                    sleep(sleepTime)
                }
            }

            if (mediaCodecDecode?.isEnd() == true) {
                isPause = true
            }
        }
    }

    fun seekTo(i: Int) {
        isSeeking = true
        seekTimeUs = 19319319L * i / 100
        println("-------------->seekTimeUs: $seekTimeUs")
    }

    fun pause() {
        isPause = !isPause
    }

    fun close() {
        isStop = true
        mediaCodecDecode?.stop()
    }
}