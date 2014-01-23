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
        config.numSamples = 2;
        config.useGL20 = true;
        config.resolutionStrategy = new FillResolutionStrategy();
        // 800, 480
        initialize(new HWBOTOpenGL(), config);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
