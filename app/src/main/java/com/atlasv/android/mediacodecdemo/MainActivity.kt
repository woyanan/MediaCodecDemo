package com.atlasv.android.mediacodecdemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.atlasv.android.mediacodecdemo.databinding.ActivityMainBinding
import com.atlasv.android.mediacodecdemo.mediacodec.VideoActivity
import com.atlasv.android.mediacodecdemo.surfacetexture.VideoActivity2
import com.atlasv.android.mediacodecdemo.surfacetexture.VideoActivity3
import com.atlasv.android.mediacodecdemo.surfacetexture2.NavigatorActivity

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
            startActivity(VideoActivity.start(view.context))
        }
        binding.buttonVideoExample2.setOnClickListener { view ->
            startActivity(VideoActivity2.start(view.context))
        }
        binding.buttonVideoExample3.setOnClickListener { view ->
            startActivity(VideoActivity3.start(view.context))
        }
        binding.buttonVideoExample4.setOnClickListener { view ->
            startActivity(Intent(view.context, NavigatorActivity::class.java))
        }

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }
}