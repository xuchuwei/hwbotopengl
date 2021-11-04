package org.hwbot.bench.gpu.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderTextTest extends BenchmarkState {

    public static final int STATE = 3;
    protected ShaderProgram program;
    private SpriteBatch batch;
    private Texture tex;

    public ShaderTextTest(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public boolean init() {
        if (initialized) {
            return true;
        }
        try {
            super.init();
            initialized = true;
            ShaderProgram.pedantic = false;

            program = new ShaderProgram(Gdx.files.internal("data/shaders/complex/passthrough.vert").readString(), Gdx.files.internal(
                    "data/shaders/complex/text.frag").readString());

            if (program.getLog().length() != 0) {
                System.out.println("shader log: " + program.getLog());
            } else {
                System.out.println("Shader initialized: " + program.getFragmentShaderSource());
            }

            batch = new SpriteBatch(1000, program);
            batch.setShader(program);

            tex = new Texture(256, 256, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

            if (!program.isCompiled()) {
                Gdx.app.log("shader", program.getLog());
            }

            camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            ((OrthographicCamera) camera).setToOrtho(false);

            return program.isCompiled();
        } catch (Exception e) {
            Gdx.app.log("error", "Failed to load OpenGL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void renderWorld() {

    }

    @Override
    public void update(float dt) {
        time += dt;
    }

    float time = 0;

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // start our batch
        batch.begin();

        // draw some sprites... they will all be affected by our shaders
        program.setUniformf("time", time);
        program.setUniform2fv("mouse", new float[] { 0.3f, 0.1f + (time / 20f) }, 0, 2);
        program.setUniform2fv("resolution", new float[] { Gdx.graphics.getWidth(), Gdx.graphics.getHeight() }, 0, 2);
        batch.draw(tex, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight(),
                Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 10);

        // end our batch
        batch.end();

    }

    @Override
    public void dispose() {
        tex.dispose();
        batch.dispose();
        program.dispose();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public int getState() {
        return STATE;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
