package tech.thdev.mediacodecexample.video

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface

/**
 * Created by woyanan on 2021/7/1
 */
class MediaCodecDecoder {
    companion object {
        private const val TAG = "MediaCodecDecode"
        private const val VIDEO = "video/"
        private const val PATH =
            "/storage/emulated/0/DCIM/Camera/ff00c75b7b424a7291ebb54780703a89.mp4"
    }

    private lateinit var extractor: MediaExtractor
    private lateinit var decoder: MediaCodec
    private val outputBufferInfo by lazy {
        MediaCodec.BufferInfo()
    }
    private var isInput = true

    fun init(surface: Surface): Boolean {
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

    fun queueInputBuffer() {
        if (isInput.not()) {
            return
        }

        println("---------------->queueInputBuffer")
        kotlin.runCatching {
            decoder.dequeueInputBuffer(1000).takeIf { it >= 0 }?.let { index ->
                // fill inputBuffers[inputBufferIndex] with valid data
                val inputBuffer = decoder.getInputBuffer(index)

                val sampleSize = extractor.readSampleData(inputBuffer!!, 0)

                if (extractor.advance() && sampleSize > 0) {
                    decoder.queueInputBuffer(index, 0, sampleSize, extractor.sampleTime, 0)
                } else {
                    Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                    decoder.queueInputBuffer(
                        index,
                        0,
                        0,
                        0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                    isInput = false
                }
            }
        }
    }

    fun dequeueOutputBuffer(render: Boolean) {
        kotlin.runCatching {
            val outIndex = decoder.dequeueOutputBuffer(outputBufferInfo, 1000)
            if (outIndex > 0) {
                decoder.releaseOutputBuffer(outIndex, render /* Surface init */)
            }
        }
    }

    fun getPresentationTimeUs(): Long {
        return outputBufferInfo.presentationTimeUs
    }

    fun isEnd(): Boolean {
        // All decoded frames have been rendered, we can stop playing now
        return if (outputBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
            Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
            true
        } else false
    }

    fun stop() {
        decoder.stop()
        decoder.release()
        extractor.release()
    }
}