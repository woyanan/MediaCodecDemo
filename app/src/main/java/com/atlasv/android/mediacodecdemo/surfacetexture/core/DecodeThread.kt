package com.atlasv.android.mediacodecdemo.surfacetexture.core

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface

/**
 * Created by woyanan on 2021/7/8
 */
class DecodeThread : Thread() {
    companion object {
        private const val VIDEO = "video/"
        private const val TAG = "VideoDecoder"
        private const val PATH =
            "/storage/emulated/0/DCIM/Camera/ff00c75b7b424a7291ebb54780703a89.mp4"
    }

    private lateinit var extractor: MediaExtractor
    private lateinit var decoder: MediaCodec
    private val decodeController by lazy { DecodeController() }

    private val outputBufferInfo = MediaCodec.BufferInfo()
    private var isStop = false
    private var isPause = false

    /// Seek
    private var isSeeking = false
    private var seekTimeUs = 0L

    fun init(surface: Surface): Boolean {
        isStop = false
        kotlin.runCatching {
            extractor = MediaExtractor()
            extractor.setDataSource(PATH)

            (0..extractor.trackCount).forEach { index ->
                val format = extractor.getTrackFormat(index)

                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith(VIDEO) == true) {
                    extractor.selectTrack(index)
                    decoder = MediaCodec.createDecoderByType(mime)
                    try {
                        Log.d(TAG, "format : $format")
                        decoder.configure(format, surface, null, 0 /* Decode */)
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "codec $mime failed configuration. $e")
                        return false
                    }

                    decoder.start()
                    return true
                }
            }
        }
        return false
    }

    override fun run() {
        var isInputDone = false
        var isOutputDone = false

        while (!isOutputDone) {
            if (isStop) {
                break
            }
            if (isPause && isSeeking.not()) {
                continue
            }
            if (!isInputDone) {
                decoder.dequeueInputBuffer(1000).takeIf { it >= 0 }?.let { index ->
                    // fill inputBuffers[inputBufferIndex] with valid data
                    val inputBuffer = decoder.getInputBuffer(index)

                    val sampleSize = extractor.readSampleData(inputBuffer!!, 0)

                    if (sampleSize < 0) {
                        Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                        decoder.queueInputBuffer(
                            index,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isInputDone = true
                    } else {
                        decoder.queueInputBuffer(index, 0, sampleSize, extractor.sampleTime, 0)
                        extractor.advance()
                    }
                }
            }

            val outIndex = decoder.dequeueOutputBuffer(outputBufferInfo, 1000)
            when (outIndex) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + decoder.outputFormat)
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.d(TAG, "INFO_TRY_AGAIN_LATER")
                }
                else -> {
                    val isRender = outIndex != 0
                    if (isSeeking) {
                        // seek时按顺序解码, 但是跳帧显示, 100ms显示一帧
                        if (outputBufferInfo.presentationTimeUs < seekTimeUs) {
                            decoder.releaseOutputBuffer(outIndex, false)
                            continue
                        }
                    } else {
                        if (isRender) {
                            decodeController.preRender(outputBufferInfo.presentationTimeUs)
                        }
                    }

                    decoder.releaseOutputBuffer(outIndex, isRender)
                }
            }
            isSeeking = false

            if (outputBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                isOutputDone = true
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

    fun release() {
        isStop = true
        decoder.stop()
        decoder.release()
        extractor.release()
    }
}