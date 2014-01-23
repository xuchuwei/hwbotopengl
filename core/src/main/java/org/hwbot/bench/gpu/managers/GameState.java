package org.hwbot.bench.gpu.managers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.utils.PerformanceCounter;

public abstract class GameState implements InputProcessor, GestureListener {

    protected boolean initialized = false;
    protected GameStateManager gsm;
    public Camera camera;
    public StringBuilder performance = new StringBuilder();
    public String instructions;
    public PerformanceCounter performanceCounter = new PerformanceCounter(this.getClass().getSimpleName());
    public FloatCounter fpsCounter = new FloatCounter(5);

    public GameState(GameStateManager gsm) {
        super();
        this.gsm = gsm;
    }

    public abstract boolean init();

    public abstract void update(float dt);

    public abstract void draw();

    public abstract void handleInput();

    public abstract void dispose();

    public abstract int getState();
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ENTER) {
            int newState = getState() + 1;
            System.out.println("STATE: " + getState() + " to " + newState);
            this.gsm.setState(newState);
        }
        return false;
    }

}
