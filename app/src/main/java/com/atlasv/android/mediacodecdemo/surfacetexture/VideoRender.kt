package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Surface
import com.atlasv.android.mediacodecdemo.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Created by woyanan on 2021/7/6
 */
class VideoRender(val context: Context) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener {
    private val vertexData = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )

    private val textureData = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )
    private val vertexBuffer by lazy {
        ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
    }
    private val textureBuffer by lazy {
        ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(textureData)
    }


    //mediacodec
    private var programMediacodec = 0
    private var avPositionMediacodec = 0
    private var afPositionMediacodec = 0
    private var samplerOESMediacodec = 0
    private var textureIdMediacodec = 0
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null

    var onSurfaceCreateListener: OnSurfaceCreateListener? = null
    var onRenderListener: OnRenderListener? = null

    init {
        vertexBuffer.position(0)
        textureBuffer.position(0)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        initRenderMediacodec()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        renderMediacodec()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        //将onFrameAvailable函数回掉到GLSurfaceView调用requestRender()触发onDrawFrame()
        onRenderListener?.onRender()
    }

    private fun initRenderMediacodec() {
        val vertexSource = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader)
        val fragmentSource = ShaderUtils.readRawTextFile(context, R.raw.fragment_mediacodec)
        programMediacodec = ShaderUtils.createProgram(vertexSource, fragmentSource)

        avPositionMediacodec = GLES20.glGetAttribLocation(programMediacodec, "av_Position")
        afPositionMediacodec = GLES20.glGetAttribLocation(programMediacodec, "af_Position")
        samplerOESMediacodec = GLES20.glGetUniformLocation(programMediacodec, "sTexture")

        val textureids = IntArray(1)
        GLES20.glGenTextures(1, textureids, 0)
        textureIdMediacodec = textureids[0]

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_REPEAT
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_REPEAT
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )

        surfaceTexture = SurfaceTexture(textureIdMediacodec)
        surface = Surface(surfaceTexture)
        surfaceTexture?.setOnFrameAvailableListener(this)

        //将Surface回调出去给MediaCodec绑定渲染
        onSurfaceCreateListener?.onSurfaceCreate(surface!!)
    }

    private fun renderMediacodec() {
        surfaceTexture?.updateTexImage()
        GLES20.glUseProgram(programMediacodec)

        GLES20.glEnableVertexAttribArray(avPositionMediacodec)
        GLES20.glVertexAttribPointer(
            avPositionMediacodec,
            2,
            GLES20.GL_FLOAT,
            false,
            8,
            vertexBuffer
        )

        GLES20.glEnableVertexAttribArray(afPositionMediacodec)
        GLES20.glVertexAttribPointer(
            afPositionMediacodec,
            2,
            GLES20.GL_FLOAT,
            false,
            8,
            textureBuffer
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIdMediacodec)
        GLES20.glUniform1i(samplerOESMediacodec, 0)
    }
}