package org.hwbot.bench.gpu.managers;

import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class GameStateManager {

    private GameState gameState;
    public static final int MENU = 0;
    private static final long BENCHMARK_TIME_SECONDS = 10;

    // BulletTestCollection
    private BitmapFont font;
    private Stage hud;
    private Label fpsLabel;
    private Label titleLabel;
    private Label instructLabel;
    private CameraInputController cameraController;

    // score
    int score = 0;

    public GameStateManager() {
        // setState(PhysicsTest.STATE);
        setState(OpenGLTest.STATE);
        // setState(ShaderLandscapeTest.STATE);
    }

    public void setState(int state) {
        System.out.println("Setting state to: " + state);
        if (gameState != null) {
            gameState.dispose();
        }

        if (state == MENU) {
            // switch to menu
        }
        if (state == PhysicsTest.STATE) {
            gameState = new PhysicsTest(this);
        }
        if (state == OpenGLTest.STATE) {
            gameState = new OpenGLTest(this);
        }
        if (state == ShaderLandscapeTest.STATE) {
            gameState = new ShaderLandscapeTest(this);
        }
        if (state == ShaderSeeTest.STATE) {
            gameState = new ShaderSeeTest(this);
        }
        if (state == ShaderShadowTest.STATE) {
            gameState = new ShaderShadowTest(this);
        }
        if (state == EndState.STATE) {
            gameState = new EndState(this);
        }
        boolean initialized = gameState.init();
        if (!initialized) {
            System.err.println("State " + gameState + " NOT INITIALIZED!");
            // setState(MENU);
        } else {
            this.init();
        }
        System.out.println("New game state: " + gameState.getClass().getSimpleName());
    }

    public void nextState() {
        if ((gameState.getState() == OpenGLTest.STATE)) {
            setState(ShaderSeeTest.STATE);
        } else if ((gameState.getState() == ShaderSeeTest.STATE)) {
            //setState(PhysicsTest.STATE);
        } else if (gameState.getState() != EndState.STATE) {
            setState(gameState.getState() + 1);
        }
    }

    private void init() {
        cameraController = new CameraInputController(gameState.camera);
        cameraController.activateKey = Keys.CONTROL_LEFT;
        cameraController.autoUpdate = false;
        cameraController.forwardTarget = false;
        cameraController.translateTarget = false;
        Gdx.input.setInputProcessor(new InputMultiplexer(cameraController, gameState, new GestureDetector(gameState)));

        font = new BitmapFont();
        // font.setScale(0.5f);
        hud = new Stage(/*480, 320, true*/);
        hud.addActor(fpsLabel = new Label(" ", new Label.LabelStyle(font, Color.WHITE)));
        fpsLabel.setPosition(0, 0);
        hud.addActor(titleLabel = new Label(gameState.getClass().getSimpleName(), new Label.LabelStyle(font, Color.WHITE)));
        titleLabel.setY(hud.getHeight() - titleLabel.getHeight());
        hud.addActor(instructLabel = new Label("A\nB\nC\nD\nE\nF", new Label.LabelStyle(font, Color.WHITE)));
        instructLabel.setY(titleLabel.getY() - instructLabel.getHeight());
        //instructLabel.setAlignment(Align.top | Align.left);
        instructLabel.setText(gameState.instructions);
    }

    public void update(float dt) {
        gameState.update(dt);
        fpsLabel.setText(gameState.performance);
        hud.draw();
        if (gameState instanceof BenchmarkState) {
            BenchmarkState state = (BenchmarkState) gameState;
            if (state.startRenderTime > 0) {
                long timelapse = System.currentTimeMillis() - state.startRenderTime;
                if (timelapse >= TimeUnit.SECONDS.toMillis(BENCHMARK_TIME_SECONDS)) {
                    System.out.println("Rendered " + state.framesRendered + " frames in " + timelapse + "ms.");
                    score += state.framesRendered;
                    nextState();
                }
            }
        }
    }

    public void draw() {
        gameState.draw();
        hud.draw();
        fpsLabel.setText(gameState.performance);
    }
}
