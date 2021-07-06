package com.atlasv.android.mediacodecdemo.surfacetexture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atlasv.android.mediacodecdemo.R

/**
 * Created by woyanan on 2021/7/6
 */
class VideoActivity2 : AppCompatActivity() {
    companion object {

        fun start(context: Context): Intent =
            Intent(context, VideoActivity2::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
    }
}