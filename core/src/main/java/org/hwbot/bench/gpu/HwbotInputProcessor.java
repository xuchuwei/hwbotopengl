package org.hwbot.bench.gpu;

import com.badlogic.gdx.InputAdapter;

public class HwbotInputProcessor extends InputAdapter {

	boolean listen;
	boolean anyKeyPressed;

	public void listen() {
		this.listen = true;
	}

	public boolean anyKeyPressed() {
		System.out.println("any key pressed? " + anyKeyPressed);
		return anyKeyPressed;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (listen) {
			anyKeyPressed = true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (listen) {
			anyKeyPressed = true;
		}
		return super.keyDown(keycode);
	}

	public void reset() {
		this.listen = false;
		this.anyKeyPressed = false;
	}

}
