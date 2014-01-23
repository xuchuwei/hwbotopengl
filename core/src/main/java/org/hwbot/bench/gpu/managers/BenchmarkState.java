package org.hwbot.bench.gpu.managers;

import org.hwbot.bench.gpu.shaders.Bloom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;

public abstract class BenchmarkState extends GameState {

    protected int debugMode = DebugDrawModes.DBG_DrawWireframe;
    public boolean loading = false;

    protected long startRenderTime;
    protected long lastRenderTime;
    protected long framesRendered;

    public BenchmarkState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public boolean init() {
        return true;
    }

    protected void debugMode() {
    }

    protected abstract void renderWorld();

    protected void beginRender(boolean lighting) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (camera != null) {
            camera.update();
        }
    }

    @Override
    public void handleInput() {

    }

    @Override
    public boolean longPress(float x, float y) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.ENTER) {
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public long getStartRenderTime() {
        return startRenderTime;
    }

    public void setStartRenderTime(long startRenderTime) {
        this.startRenderTime = startRenderTime;
    }

    public long getLastRenderTime() {
        return lastRenderTime;
    }

    public void setLastRenderTime(long endRenderTime) {
        this.lastRenderTime = endRenderTime;
    }

    public long getFramesRendered() {
        return framesRendered;
    }

    public void setFramesRendered(long framesRendered) {
        this.framesRendered = framesRendered;
    }

}
