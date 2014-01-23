package org.hwbot.bench.gpu;

import org.hwbot.bench.opengl.core.HWBOTOpenGL;
import org.robovm.cocoatouch.foundation.NSAutoreleasePool;
import org.robovm.cocoatouch.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class RobovmLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        config.preferredFramesPerSecond = 10000;
        // config.colorFormat = GLKViewDrawableColorFormat.RGB565;
        return new IOSApplication(new HWBOTOpenGL(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, RobovmLauncher.class);
        pool.drain();
    }
}
