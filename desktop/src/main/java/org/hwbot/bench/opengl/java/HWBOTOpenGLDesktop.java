package org.hwbot.bench.opengl.java;

import org.hwbot.bench.opengl.core.HWBOTOpenGL;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class HWBOTOpenGLDesktop {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.useGL20 = true;
        config.width = 1280;
        config.height = 720;
        // config.width = 800;
        // config.height = 480;
        config.resizable = false;
//        config.samples = 8;
//        config.r = 5;
//        config.g = 6;
//        config.b = 5;
//        config.a = 0;
//        config.fullscreen = true;
        config.foregroundFPS = 0;
        // config.fullscreen = true;
        config.backgroundFPS = 0;
        config.vSyncEnabled = false;

        new LwjglApplication(new HWBOTOpenGL(), config);
    }
}
