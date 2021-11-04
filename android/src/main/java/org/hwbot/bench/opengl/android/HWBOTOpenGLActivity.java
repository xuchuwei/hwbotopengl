package org.hwbot.bench.opengl.android;

import org.hwbot.bench.opengl.core.HWBOTOpenGL;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;

public class HWBOTOpenGLActivity extends AndroidApplication {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        // FSAA
        // config.r = 8;
        // config.g = 8;
        // config.b = 8;
        // config.a = 2;

        /** number of bits for depth and stencil buffer **/
        // config.depth = 16;
        // config.stencil = 4;
        config.numSamples = 0;
        //config.useGL30 = true;
        config.resolutionStrategy = new FillResolutionStrategy();
        // config.resolutionStrategy = new FixedResolutionStrategy(800, 480);
        // 800, 480
        initialize(new HWBOTOpenGL(), config);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
