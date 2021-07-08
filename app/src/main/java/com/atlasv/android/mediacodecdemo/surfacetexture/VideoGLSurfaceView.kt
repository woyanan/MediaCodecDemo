package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * Created by woyanan on 2021/7/6
 */
class VideoGLSurfaceView : GLSurfaceView {
    var render: VideoRender2

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setEGLContextClientVersion(2)
        render = VideoRender2(context)
        setRenderer(render)
//        renderMode = RENDERMODE_WHEN_DIRTY
//        render.onRenderListener = object : OnRenderListener {
//            override fun onRender() {
//                requestRender()
//            }
//        }
    }

}