package com.atlasv.android.meishe.fbo

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.meishe.R
import com.atlasv.android.meishe.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_opengl_player.*


/**
 * 灵魂出窍播放
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-26 21:07
 *
 */
class SoulPlayerActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context, path: String) {
            val intent = Intent(context, SoulPlayerActivity::class.java)
            intent.putExtra(CommonUtil.PATH, path)
            context.startActivity(intent)
        }
    }

    private lateinit var drawer: IDrawer
    private val mediaPlayer by lazy { MediaPlayer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_player)
        initRender()
        ivPlay?.setOnClickListener {
            mediaPlayer.start()
        }
    }

    private fun initRender() {
        drawer = SoulVideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(Surface(it))
        }
        gl_surface.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        gl_surface.setRenderer(render)
    }

    private fun initPlayer(sf: Surface) {
        mediaPlayer.setSurface(sf)
        sf.release()
        kotlin.runCatching {
            val path = intent?.getStringExtra(CommonUtil.PATH)
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepareAsync()
        }
    }
}