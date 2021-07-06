package com.atlasv.android.mediacodecdemo.mediacodec

/**
 * Created by woyanan on 2021/7/1
 */
class MediaDecodeThread(private val mediaCodecDecode: MediaCodecDecoder?) : Thread() {
    private var isStop = false
    private var isPause = false

    override fun run() {
        while (true) {
            if (isStop) {
                break
            }
            if (isPause) {
                continue
            }
            mediaCodecDecode?.queueInputBuffer()
//            mediaCodecDecode?.
        }
    }

    fun seekTo(i: Int) {
        isPause = false
    }

    fun pause() {
        isPause = !isPause
    }

    fun close() {
        isStop = true
        mediaCodecDecode?.stop()
    }
}