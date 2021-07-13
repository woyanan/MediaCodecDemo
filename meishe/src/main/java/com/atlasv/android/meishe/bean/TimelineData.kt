package com.atlasv.android.meishe.bean

import android.graphics.Point
import com.atlasv.android.meishe.utils.NvAsset
import com.meicam.sdk.NvsVideoResolution
import java.util.*

/**
 * Created by woyanan on 2021/7/13
 */
class TimelineData {
    companion object {
        const val CompileVideoRes_1080 = 1080

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TimelineData()
        }
    }

    var videoResolution: NvsVideoResolution? = null
    var clipInfoList = ArrayList<ClipInfo>()
    var makeRatio = NvAsset.AspectRatio_9v16

    init {
        initVideoResolution()
    }

    private fun initVideoResolution() {
        videoResolution = NvsVideoResolution()
        val size = Point()
        size[CompileVideoRes_1080] = CompileVideoRes_1080 * 16 / 9
        videoResolution?.imageWidth = size.x
        videoResolution?.imageHeight = size.y
        videoResolution?.bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT
    }
}