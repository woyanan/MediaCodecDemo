package com.atlasv.android.mediacodec

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.atlasv.android.mediacodec.surfacetexture.MediaPlayerActivity
import com.atlasv.android.mediacodec.surfacetexture.MediaCodecActivity
import com.atlasv.android.mediacodecdemo.databinding.ActivityMainBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonVideoExample.setOnClickListener { view ->
            startActivity(MediaPlayerActivity.start(view.context))
        }
        binding.buttonVideoExample2.setOnClickListener { view ->
            startActivity(MediaCodecActivity.start(view.context))
//            startActivity(Intent(view.context, NavigatorActivity::class.java))
        }

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }
}