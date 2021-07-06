package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by woyanan on 2021/7/6
 */
object ShaderUtils {

    fun readRawTextFile(context: Context, resId: Int): String? {
        val inputStream = context.resources.openRawResource(resId)
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
            return sb.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES20.glCreateShader(shaderType)
        if (shader != 0) {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    fun createProgram(vertexSource: String?, fragmentSource: String?): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }
        var program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            GLES20.glAttachShader(program, pixelShader)
            checkGlError("glAttachShader")
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    fun checkGlError(label: String) {
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            throw RuntimeException("$label: glError $error")
        }
    }
}