package org.hwbot.bench.opengl.html;

import org.hwbot.bench.opengl.core.HWBOTOpenGL;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HWBOTOpenGLHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new HWBOTOpenGL();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
