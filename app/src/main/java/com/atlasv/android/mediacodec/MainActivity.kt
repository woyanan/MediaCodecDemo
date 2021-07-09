package com.atlasv.android.mediacodec

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.atlasv.android.mediacodec.databinding.ActivityMainBinding
import com.atlasv.android.mediacodec.surfacetexture.MediaCodecActivity
import com.atlasv.android.mediacodec.surfacetexture.MediaPlayerActivity
import com.atlasv.android.mediacodec.surfacetexture.utils.CommonUtil


class MainActivity : AppCompatActivity() {
    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
            return permissions.find {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            } == null
        }
    }

    private lateinit var binding: ActivityMainBinding
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        }
    private val pickVideoLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            val path = CommonUtil.contentUri2FilePath(this, uri)
            if (!path.isNullOrEmpty()) {
                when (value) {
                    Enum.MEDIA_PLAYER -> {
                        MediaPlayerActivity.start(this, path)
                    }
                    Enum.MEDIACODEC -> {
                        MediaCodecActivity.start(this, path)
                    }
                }
            }
        }
    }
    private var value = Enum.MEDIA_PLAYER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mediaPlayer.setOnClickListener {
            value = Enum.MEDIA_PLAYER
            pickVideo()
        }
        binding.mediacodec.setOnClickListener {
            value = Enum.MEDIACODEC
            pickVideo()
        }
        requestPermission()
    }

    private fun requestPermission() {
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun pickVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickVideoLauncher.launch(intent)
    }

    enum class Enum {
        MEDIA_PLAYER, MEDIACODEC
    }
}