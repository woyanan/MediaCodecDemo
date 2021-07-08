package com.atlasv.android.mediacodecdemo.surfacetexture.core

/**
 * Created by woyanan on 2021/7/8
 */
class DecodeController {
    companion object {
        private const val ONE_MILLION = 1000000L
    }

    private var prevPresentUs = 0L
    private var prevMonoUs = 0L
    private var fixedFrameDurationUs = 0L

    fun preRender(presentationTimeUs: Long) {
        if (prevMonoUs == 0L) {
            prevMonoUs = System.nanoTime() / 1000
            prevPresentUs = presentationTimeUs
        } else {
            var frameDelta = if (fixedFrameDurationUs != 0L) {
                fixedFrameDurationUs
            } else {
                presentationTimeUs - prevPresentUs
            }
            when {
                frameDelta < 0 -> {
                    frameDelta = 0
                }
                frameDelta == 0L -> {
                }
                frameDelta > 10 * ONE_MILLION -> {
                    frameDelta = 5 * ONE_MILLION
                }
            }
            val desiredUs = prevMonoUs + frameDelta
            var nowUs = System.nanoTime() / 1000
            while (nowUs < desiredUs - 100) {
                var sleepTimeUs = desiredUs - nowUs
                if (sleepTimeUs > 500000) {
                    sleepTimeUs = 500000
                }
                kotlin.runCatching {
                    Thread.sleep(sleepTimeUs / 1000, (sleepTimeUs % 1000).toInt() * 1000)
                }
                nowUs = System.nanoTime() / 1000
            }

            prevMonoUs += frameDelta
            prevPresentUs += frameDelta
        }
    }

    fun reset() {
        prevPresentUs = 0L
        prevMonoUs = 0L
        fixedFrameDurationUs = 0L
    }
}