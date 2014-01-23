package org.hwbot.bench.opengl.core;

import org.hwbot.bench.gpu.HwbotInputProcessor;
import org.hwbot.bench.gpu.managers.GameStateManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public class HWBOTOpenGL implements ApplicationListener {

    private GameStateManager gsm;
    public static int WIDTH;
    public static int HEIGHT;

    @Override
    public void create() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        System.out.println("w: " + WIDTH + "*" + HEIGHT);

        Gdx.input.setInputProcessor(new HwbotInputProcessor());

        gsm = new GameStateManager();

        System.out.println("Created and initializing bullet physics!");
    }

    @Override
    public void render() {
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.draw();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
