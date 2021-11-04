package org.hwbot.bench.gpu.shaders;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class CustomShader {

    Texture tex;
    SpriteBatch batch;
    OrthographicCamera cam;
    ShaderProgram shader;

    public CustomShader() {
        initialize();
    }

    private void initialize() {
        // important since we aren't using some uniforms and attributes that SpriteBatch expects
        ShaderProgram.pedantic = false;

        shader = CustomShaderLoader.createShader();
        if (!shader.isCompiled()) {
            System.err.println(shader.getLog());
            System.exit(0);
        }
        if (shader.getLog().length() != 0)
            System.out.println(shader.getLog());
    }

    public void capture() {
    }

    /**
     * Call this after scene. Renders the bloomed scene.
     */
    public void render() {

    }

    public void dispose() {

    }

}
