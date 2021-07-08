package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import com.atlasv.android.mediacodecdemo.R
import com.atlasv.android.mediacodecdemo.surfacetexture2.utils.RawResourceReader
import com.atlasv.android.mediacodecdemo.surfacetexture2.utils.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by woyanan on 2021/7/7
 */
class VideoRenderer(val context: Context) : GLSurfaceView.Renderer,
    SurfaceTexture.OnFrameAvailableListener {
    private var shaderProgram = 0
    private var textureParamHandle = 0
    private var textureCoordinateHandle = 0
    private var positionHandle = 0
    private var textureTransformHandle = 0

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)
    private var drawListBuffer: ShortBuffer? = null

    private val squareSize = 1.0f
    private val squareCoordinate = floatArrayOf(
        -squareSize, squareSize,  // top left
        -squareSize, -squareSize,  // bottom left
        squareSize, -squareSize,  // bottom right
        squareSize, squareSize   // top right
    )

    private var vertexBuffer: FloatBuffer? = null

    private var textureBuffer: FloatBuffer? = null
    private val textureCoordinate = floatArrayOf(
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f
    )
    private val textures = IntArray(1)

    private var width = 0
    private var height = 0
    private var frameAvailable = false
    private val videoTextureTransform = FloatArray(16)

    var videoTexture: SurfaceTexture? = null

    var onSurfaceChanged: (() -> Unit)? = null

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        println("----------------->onSurfaceCreated")
        setupGraphics()
        setupVertexBuffer()
        setupTexture()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        println("----------------->onSurfaceChanged")
        this.width = width
        this.height = height
        onSurfaceChanged?.invoke()
    }

    override fun onDrawFrame(p0: GL10?) {
        println("----------------->onDrawFrame")
        synchronized(this) {
            if (frameAvailable) {
                videoTexture?.updateTexImage()
                videoTexture?.getTransformMatrix(videoTextureTransform)
                frameAvailable = false
            }
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glViewport(0, 0, width, height)
        drawTexture()
    }

    private fun setupGraphics() {
        val vertexShader =
            RawResourceReader.readTextFileFromRawResource(context, R.raw.vetext_sharder)
        val fragmentShader =
            RawResourceReader.readTextFileFromRawResource(context, R.raw.fragment_sharder)

        val vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle =
            ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        shaderProgram = ShaderHelper.createAndLinkProgram(
            vertexShaderHandle,
            fragmentShaderHandle,
            arrayOf("texture", "vPosition", "vTexCoordinate", "textureTransform")
        )

        GLES20.glUseProgram(shaderProgram)
        textureParamHandle = GLES20.glGetUniformLocation(shaderProgram, "texture")
        textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinate")
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        textureTransformHandle = GLES20.glGetUniformLocation(shaderProgram, "textureTransform")
    }

    private fun setupVertexBuffer() {
        // Draw list buffer
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer?.put(drawOrder)
        drawListBuffer?.position(0)

        // Initialize the texture holder
        val byteBuffer = ByteBuffer.allocateDirect(squareCoordinate.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer?.put(squareCoordinate)
        vertexBuffer?.position(0)
    }

    private fun setupTexture() {
        val byteBuffer = ByteBuffer.allocateDirect(textureCoordinate.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        textureBuffer = byteBuffer.asFloatBuffer()
        textureBuffer?.put(textureCoordinate)
        textureBuffer?.position(0)

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glGenTextures(1, textures, 0)
        checkGlError("Texture generate")
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        checkGlError("Texture bind")

        videoTexture = SurfaceTexture(textures[0])
        videoTexture?.setOnFrameAvailableListener(this)
    }

    private fun checkGlError(op: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error))
        }
    }


    /**
     * Draw texture
     */
    private fun drawTexture() {
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(textureParamHandle, 0)
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle)
        GLES20.glVertexAttribPointer(
            textureCoordinateHandle,
            4,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        GLES20.glUniformMatrix4fv(textureTransformHandle, 1, false, videoTextureTransform, 0)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLE_STRIP,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle)
    }

    override fun onFrameAvailable(p0: SurfaceTexture?) {
        println("----------------->onFrameAvailable")
        synchronized(this) {
            frameAvailable = true
        }
    }

}