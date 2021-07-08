package com.atlasv.android.mediacodecdemo.surfacetexture2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atlasv.android.mediacodecdemo.R;
import com.atlasv.android.mediacodecdemo.surfacetexture2.texture.TextureViewMediaActivity;

/**
 * PlayVideo-OpenGL
 */
public class NavigatorActivity extends AppCompatActivity implements View.OnClickListener {

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        mIntent = new Intent();
        findViewById(R.id.button_texture_view).setOnClickListener(this);
        findViewById(R.id.button_glsurfaceview).setOnClickListener(this);
        findViewById(R.id.button_glsurface_view_native).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_texture_view:
                mIntent.setClass(this, TextureViewMediaActivity.class);
                startActivity(mIntent);
                Toast.makeText(this, "Play Video on TextureView", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_glsurfaceview:
                mIntent.setClass(this, GLViewMediaActivity.class);
                startActivity(mIntent);
                Toast.makeText(this, "Play Video on GLSurfaceView", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_glsurface_view_native:
                Toast.makeText(this, "prompt: this activity need to config ndk", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Play Video on Native", Toast.LENGTH_SHORT).show();
                mIntent.setClass(this, NativeMediaActivity.class);
                startActivity(mIntent);
                break;
        }
    }
}
