package org.hwbot.bench.gpu.managers;

import org.hwbot.bench.gpu.shaders.Bloom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20.;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class OpenGLTest extends BenchmarkState {

    public static final int STATE = 1;

    public AssetManager assets;
    public boolean loading = false;

    public ModelBatch modelBatch;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public Environment environment;
    public ModelInstance landscape, sky;
    public ModelBuilder modelBuilder = new ModelBuilder();

    private String landscapefile = "trees/Island_Cartoon_Level_Collection.g3db";
    private Model modelModel;
    private Model treeModel1;
    private Model landscapeModel;

    // shader
    private static final float HALF_PI = MathUtils.PI / 2.f;
    public static final float INFINITY = 3.3e+38f;
    public static final float RAD_PER_SEC = 0.000072921150f;
    /** Distribution coefficients for the luminance(Y) distribution function */
    private float distributionLuminance[][] = { // Perez distributions
    { 0.17872f, -1.46303f }, // a = darkening or brightening of the horizon
            { -0.35540f, 0.42749f }, // b = luminance gradient near the horizon,
            { -0.02266f, 5.32505f }, // c = relative intensity of the
                                     // circumsolar region
            { 0.12064f, -2.57705f }, // d = width of the circumsolar region
            { -0.06696f, 0.37027f } }; // e = relative backscattered light
    /** Distribution coefficients for the x distribution function */
    private float distributionXcomp[][] = { { -0.01925f, -0.25922f }, { -0.06651f, 0.00081f }, { -0.00041f, 0.21247f }, { -0.06409f, -0.89887f },
            { -0.00325f, 0.04517f } };
    /** Distribution coefficients for the y distribution function */
    private float distributionYcomp[][] = { { -0.01669f, -0.26078f }, { -0.09495f, 0.00921f }, { -0.00792f, 0.21023f }, { -0.04405f, -1.65369f },
            { -0.01092f, 0.05291f } };
    /** Zenith x value */
    private float zenithXmatrix[][] = { { 0.00165f, -0.00375f, 0.00209f, 0.00000f }, { -0.02903f, 0.06377f, -0.03202f, 0.00394f },
            { 0.11693f, -0.21196f, 0.06052f, 0.25886f } };
    /** Zenith y value */
    private float zenithYmatrix[][] = { { 0.00275f, -0.00610f, 0.00317f, 0.00000f }, { -0.04214f, 0.08970f, -0.04153f, 0.00516f },
            { 0.15346f, -0.26756f, 0.06670f, 0.26688f } };

    private float thetaSun = MathUtils.degreesToRadians * 10.f;
    private float phiSun;
    private float turbidity = 2.f;
    private float overcast;
    // private boolean isLinearExpControl;
    private float exposure;
    private float gammaCorrection;
    private float zenithLuminance;
    private float zenithX;
    private float zenithY;
    private float[] perezLuminance;
    private float[] perezX;
    private float[] perezY;
    private Vector3 sunDirection = new Vector3();

    // bench
    private boolean stop;
    private Texture skyTexture;
    protected CatmullRomSpline<Vector3> myCatmull;
    protected Vector3[] dataSet;

    // shaders
    // sky
    protected Mesh quad;
    protected ShaderProgram atmosphereShader;
    protected Bloom bloom;

    public OpenGLTest(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public boolean init() {
        if (initialized) {
            return true;
        }
        try {
            super.init();
            quad = new Mesh(true, 4, 0, new VertexAttribute(Usage.Position, 2, "a_position"));

            // Set our verts up in a CCW (Counter Clock Wise) order
            quad.setVertices(new float[] { -5f, -5f, // bottom left
                    5f, -5f, // bottom right
                    5f, 5f, // top right
                    -5f, 5f }); // top left

            quad.setAutoBind(true);
            loadShaderPrograms();

            initialized = true;
            environment = new Environment();
            environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
            environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

            // path camera
            dataSet = new Vector3[10];
            dataSet[0] = new Vector3(0, 30f, 100f);
            dataSet[1] = new Vector3(0, 35f, 100f);
            // dataSet[2] = new Vector3(0, 20f, 130f);
            // dataSet[3] = new Vector3(0, 20f, 130f);
            // dataSet[4] = new Vector3(0, 20f, 100f);
            // dataSet[5] = new Vector3(5, 20f, 100f);
            // dataSet[6] = new Vector3(19, 30f, 80f);
            // dataSet[7] = new Vector3(30, 30f, 50f);
            // dataSet[8] = new Vector3(40, 40f, 60f);
            // dataSet[9] = new Vector3(20, 50f, 70f);

            myCatmull = new CatmullRomSpline<Vector3>(dataSet, true);
            System.out.println("myCatmull.spanCount: " + myCatmull.spanCount);

            camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(0f, 30f, 100f);
            // camera.position.set(0f, 70f, 250f);
            camera.lookAt(0, 5, -7);
            camera.near = 0.1f;
            camera.far = 20000f;
            camera.update();

            assets = new AssetManager();
            assets.load("data/" + landscapefile, Model.class);

            modelBatch = new ModelBatch();

            loading = true;
            return true;
        } catch (Exception e) {
            Gdx.app.log("error", "Failed to load OpenGL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    void loadShaderPrograms() {
        try {
            atmosphereShader = new ShaderProgram(Gdx.files.internal("data/shaders/vert.glsl").readString(), Gdx.files.internal("data/shaders/frag.glsl")
                    .readString());
            if (!atmosphereShader.isCompiled()) {
                throw new IllegalStateException(atmosphereShader.getLog());
            } else {
                System.out.println("Shader loaded!");
            }
            // bloom = new Bloom(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, true, false, true);
            bloom = new Bloom();
            bloom.setBloomIntesity(0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    float lastx;
    float lastz;

    int tooslow = 0;
    int maxslow = 5;
    float maxrendertime = 0.05f;
    long stoptime = Long.MAX_VALUE;
    long startrendertime;
    long endrendertime;
    int frames;

    float current = 0;
    float speed = 0.15f;

    @Override
    public void update(float dt) {
        if (!loading && !stop) {
            // System.out.println("pos start: " + camera.position);
            if (lapse + dt > 0.03) {
                camera.rotateAround(new Vector3(0, 0, 0), Vector3.Y, 0.20f);
                /**
                 * int k = 2; current += Gdx.graphics.getDeltaTime() * speed; if (current >= 1) { current -= 1; } float place = current * k; Vector3 first =
                 * dataSet[(int) place]; System.out.println("myCatmull: " + myCatmull + " first" + first + " plcace: " + place); Vector3 cameraPosition =
                 * myCatmull.valueAt(first, current); camera.position.x = cameraPosition.x; camera.position.y = cameraPosition.y; camera.position.z =
                 * cameraPosition.z; System.out.println("pos: " + (int) place + " " + camera.position + " current: " + current);
                 **/
                camera.update();
                if (startrendertime == 0) {
                    startrendertime = System.currentTimeMillis();
                }
                lapse = 0f;
            } else {
                lapse += dt;
            }
            frames++;
        }
    }

    float lapse = 0;

    private Model skyModel;

    @Override
    public void draw() {
        if (loading) {
            if (assets.update()) {
                doneLoading();
            }
        }

        if (!loading) {
            calculateSunShader();
            bloom.capture();
            startSunShader();
        }

        fpsCounter.put(Gdx.graphics.getFramesPerSecond());
        beginRender(true);
        if (!loading) {
            quad.render(atmosphereShader, GL20.GL_TRIANGLE_FAN);
            renderWorld();
            lastRenderTime = System.currentTimeMillis();
            framesRendered++;
            endSunShader();
            bloom.render();
            performance.setLength(0);
            performance.append("FPS: ").append(fpsCounter.value);
        } else {
            performance.setLength(0);

            // GL.glGetString(GL.GL_VENDOR) and GL.glGetString(GL.GL_VERSION)
            GL20 gl = Gdx.gl20;
            Gdx.app.log("OpenGL", "OGL renderer (best guess videocard, okay for nvidia, not for ati): " + gl.glGetString(GL20.GL_RENDERER));
            Gdx.app.log("OpenGL", "OGL vendor (manufacturer): " + gl.glGetString(GL20.GL_VENDOR));
            // Gdx.app.log("AndroidGraphics", "OGL version (driver?): " + gl.glGetString(GL20.GL_VERSION));
            // Gdx.app.log("AndroidGraphics", "OGL extensions (supported features): " + gl.glGetString(GL20.GL_EXTENSIONS));
            // Gdx.app.log("AndroidGraphics", "OGL 0x21A4: " + gl.glGetString(0x21A4));
            performance.append("Loading using " + gl.glGetString(GL20.GL_VENDOR) + ", " + gl.glGetString(GL20.GL_VERSION) + ", "
                    + gl.glGetString(GL20.GL_RENDERER) + "...");

            String feats[] = gl.glGetString(GL20.GL_EXTENSIONS).split(" ");
            for (int i = 0; i < feats.length; i++) {
                if ("GLX_AMD_gpu_association".equals(feats[i])) {
                    System.out.println("AMD videocard");
                } else if (feats[i].toLowerCase().contains("nvidia")) {
                    System.out.println("nvidia: " + feats[i]);
                } else {
                    System.out.println(feats[i]);
                }
            }
        }
    }

    public void calculateSunShader() {
        thetaSun += (RAD_PER_SEC * 1000) / Gdx.graphics.getFramesPerSecond();
        if (thetaSun > HALF_PI + (MathUtils.degreesToRadians * 30.f)) {
            thetaSun = 0;
        }
        phiSun = MathUtils.degreesToRadians * 90.f;

        turbidity = 2.f;
        turbidity = MathUtils.clamp(turbidity, 1.0f, 512.0f);

        overcast = 0.f;
        overcast = MathUtils.clamp(overcast, 0.0f, 1.0f);

        exposure = 18.f;
        exposure = 1.0f / MathUtils.clamp(exposure, 1.0f, INFINITY);

        // Start fading out gammaCorrection after sun passes 70 degrees
        gammaCorrection = 1.f / MathUtils.clamp(2.5f * ((MathUtils.degreesToRadians * 70.f) / thetaSun), 1.f, 2.5f);

        // gammaCorrection = 1.0f / FastMath.clamp(gammaCorrection, 1.f,
        // INFINITY);

        float chi = ((4.0f / 9.0f) - (turbidity / 120.0f)) * (MathUtils.PI - (2.0f * thetaSun));
        zenithLuminance = ((4.0453f * turbidity) - 4.9710f) * tan(chi) - (0.2155f * turbidity) + 2.4192f;
        if (zenithLuminance < 0.0f) {
            zenithLuminance = -zenithLuminance;
        }

        sunDirection.x = MathUtils.cos(HALF_PI - thetaSun) * MathUtils.cos(phiSun);
        sunDirection.y = MathUtils.sin(HALF_PI - thetaSun);
        sunDirection.z = MathUtils.cos(HALF_PI - thetaSun) * MathUtils.sin(phiSun);
        sunDirection.nor();

        // get x / y zenith
        zenithX = getZenith(zenithXmatrix, thetaSun, turbidity);
        zenithY = getZenith(zenithYmatrix, thetaSun, turbidity);

        // get perez function parameters
        perezLuminance = getPerez(distributionLuminance, turbidity);
        perezX = getPerez(distributionXcomp, turbidity);
        perezY = getPerez(distributionYcomp, turbidity);

        // make some precalculation
        zenithX = perezFunctionO1(perezX, thetaSun, zenithX);
        zenithY = perezFunctionO1(perezY, thetaSun, zenithY);
        zenithLuminance = perezFunctionO1(perezLuminance, thetaSun, zenithLuminance);
    }

    private void startSunShader() {
        atmosphereShader.begin();

        atmosphereShader.setUniform3fv("windowDimensions", new float[] { Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0.f }, 0, 3);
        atmosphereShader.setUniform3fv("sunDirection", new float[] { sunDirection.x, sunDirection.y, sunDirection.z }, 0, 3);
        atmosphereShader.setUniform3fv("zenithData", new float[] { zenithX, zenithY, zenithLuminance }, 0, 3);
        atmosphereShader.setUniform1fv("perezLuminance", perezLuminance, 0, perezLuminance.length);
        atmosphereShader.setUniform1fv("perezX", perezX, 0, perezX.length);
        atmosphereShader.setUniform1fv("perezY", perezY, 0, perezY.length);
        atmosphereShader.setUniform3fv("colourCorrection", new float[] { exposure, overcast, gammaCorrection }, 0, 3);
    }

    private void endSunShader() {
        atmosphereShader.end();
    }

    protected void renderWorld() {
        if (modelBatch != null) {
            if (startRenderTime == 0 && !loading) {
                System.out.println("start rending time");
                startRenderTime = System.currentTimeMillis();
            }
            modelBatch.begin(camera);
            modelBatch.render(instances, environment);
            if (landscape != null) {
                modelBatch.render(landscape);
            }
            modelBatch.end();
        } else {
            Gdx.app.log("error", "Modelbatch missing.");
        }
    }

    private void doneLoading() {
        System.out.println("Done loading assets.");
        // modelModel = assets.get("data/" + modelfile, Model.class);
        //
        // instances.add(addObject(modelModel, 0f, 29f, 0f, 0f, 0f, 0f));
        // instances.add(addObject(modelModel, 10f, 27.1f, 0f, 15f, 50f, -30f));

        landscapeModel = assets.get("data/" + landscapefile, Model.class);
        landscape = new ModelInstance(landscapeModel);
        landscape.transform.rotate(0, 0, 0, 0f);
        landscape.transform.scale(1f, 1f, 1f);
        // landscape.transform.translate(0f, -2.9f, 0f);
        landscape.transform.translate(0f, 0f, 0f);
        System.out.println("materials: " + landscape.materials.size);
        for (int i = 0; i < landscape.materials.size; i++) {
            landscape.materials.get(i).set(ColorAttribute.createDiffuse(new Color(0.80f, 0.80f, 0.80f, 1)));
        }
        loading = false;
    }

    protected ModelInstance addObject(Model modelModel, float x, float y, float z, float xdegrees, float ydegrees, float zdegrees) {
        ModelInstance model = new ModelInstance(modelModel);
        // model.transform.scale(1f, 1f, 1f);
        model.transform.translate(x, y, z);
        if (xdegrees > 0) {
            model.transform.rotate(1, 0, 0, xdegrees);
        }
        if (ydegrees > 0) {
            model.transform.rotate(0, 1, 0, ydegrees);
        }
        if (zdegrees > 0) {
            model.transform.rotate(0, 0, 1, zdegrees);
        }
        model.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.66f, 0.66f, 0.66f, 1)));
        return model;
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {
        System.out.println("dispose");
        stop = true;
        modelBatch.dispose();
        if (modelModel != null) {
            modelModel.dispose();
        }
        if (treeModel1 != null) {
            treeModel1.dispose();
        }
        if (landscapeModel != null) {
            landscapeModel.dispose();
        }
        if (skyModel != null) {
            skyModel.dispose();
        }
        if (skyTexture != null) {
            skyTexture.dispose();
        }
        quad.dispose();
        atmosphereShader.dispose();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("tap next");
        // this.gsm.setState(PhysicsTest.STATE);
        return true;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public int getState() {
        return STATE;
    }

    @Override
    protected void debugMode() {
        super.debugMode();
    }

    // / shader
    public static float exp(float fValue) {
        return (float) Math.exp(fValue);
    }

    public static float tan(float fValue) {
        return (float) Math.tan(fValue);
    }

    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    private float getZenith(float[][] zenithMatrix, float theta, float turbidity) {
        float theta2 = theta * theta;
        float theta3 = theta * theta2;

        return (zenithMatrix[0][0] * theta3 + zenithMatrix[0][1] * theta2 + zenithMatrix[0][2] * theta + zenithMatrix[0][3]) * turbidity * turbidity
                + (zenithMatrix[1][0] * theta3 + zenithMatrix[1][1] * theta2 + zenithMatrix[1][2] * theta + zenithMatrix[1][3]) * turbidity
                + (zenithMatrix[2][0] * theta3 + zenithMatrix[2][1] * theta2 + zenithMatrix[2][2] * theta + zenithMatrix[2][3]);
    }

    private float[] getPerez(float[][] distribution, float turbidity) {
        float[] perez = new float[5];
        perez[0] = distribution[0][0] * turbidity + distribution[0][1];
        perez[1] = distribution[1][0] * turbidity + distribution[1][1];
        perez[2] = distribution[2][0] * turbidity + distribution[2][1];
        perez[3] = distribution[3][0] * turbidity + distribution[3][1];
        perez[4] = distribution[4][0] * turbidity + distribution[4][1];
        return perez;
    }

    private float perezFunctionO1(float[] perezCoeffs, float thetaSun, float zenithValue) {
        float val = (1.0f + perezCoeffs[0] * exp(perezCoeffs[1]))
                * (1.0f + perezCoeffs[2] * exp(perezCoeffs[3] * thetaSun) + perezCoeffs[4] * sqr(MathUtils.cos(thetaSun)));
        return zenithValue / val;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
