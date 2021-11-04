package org.hwbot.bench.gpu.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hwbot.bench.bullet.BulletConstructor;
import org.hwbot.bench.bullet.BulletEntity;
import org.hwbot.bench.bullet.BulletWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class PhysicsTest extends BenchmarkState implements InputProcessor, GestureListener {

    public static final int STATE = 5;

    // BulletTest
    public String instructions = "Tap to shoot\nLong press to toggle debug mode\nSwipe for next test\nCtrl+drag to rotate\nScroll to zoom";

    // BaseBulletTest
    public Environment lights;
    public DirectionalLight shadowLight;
    public ModelBatch shadowBatch;
    public BulletWorld world;
    public ObjLoader objLoader = new ObjLoader();
    public ModelBuilder modelBuilder = new ModelBuilder();
    public ModelBatch modelBatch;
    public Array<Disposable> disposables = new Array<Disposable>();

    // ShootTest
    final Color white = new Color(1, 1, 1, 1f);
    final Color blue = new Color(0, 0, 1, 1f);
    final Color red = new Color(1, 0, 0, 1f);
    final Color green = new Color(0, 1, 0, 1f);

    final float BOXOFFSET_X = 1.1f;
    final float BOXOFFSET_Y = 1.0f;
    final float BOXOFFSET_Z = 1.0f;

    BulletEntity ground;

    private Texture boxTexture;

    private Texture box2Texture;

    private List<Vector3> targets = new ArrayList<Vector3>();

    public PhysicsTest(GameStateManager gsm) {
        super(gsm);
        instructions = "Shoot tanks!";
    }

    @Override
    public boolean init() {
        if (initialized)
            return true;
        // Need to initialize bullet before using it.
        try {
            super.init();
            Bullet.init();
            Gdx.app.log("Bullet", "Version = " + LinearMath.btGetVersion());

            // BaseBullet
            lights = new Environment();
            lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
            lights.add((shadowLight = new DirectionalLight()).set(0.8f, 0.8f, 0.8f, 0.5f, -1f, -0.7f));
            modelBatch = new ModelBatch();

            world = createWorld();
            world.performanceCounter = performanceCounter;

            final float width = Gdx.graphics.getWidth();
            final float height = Gdx.graphics.getHeight();
            if (width > height)
                camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
            else
                camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
            camera.position.set(0f, 8f, 20f);
            camera.lookAt(10, 3, 0);
            camera.update();

            // Create some simple models

            boxTexture = new Texture(Gdx.files.internal("data/physics/granite.jpg"));
            disposables.add(boxTexture);
            final Material boxMaterial = new Material(TextureAttribute.createDiffuse(boxTexture), ColorAttribute.createSpecular(1, 1, 1, 1),
                    FloatAttribute.createShininess(8f));
            box2Texture = new Texture(Gdx.files.internal("data/physics/granite_dark.jpg"));
            disposables.add(box2Texture);
            final Material box2Material = new Material(TextureAttribute.createDiffuse(box2Texture), ColorAttribute.createSpecular(1, 1, 1, 1),
                    FloatAttribute.createShininess(8f));

            final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
            final Model groundModel = modelBuilder.createRect(80f, 0f, -80f, -80f, 0f, -80f, -80f, 0f, 80f, 80f, 0f, 80f, 0, 1, 0,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(16f)),
                    Usage.Position | Usage.Normal);
            disposables.add(groundModel);
            final Model boxModel = modelBuilder.createBox(1f, 1f, 1f, boxMaterial, attributes);
            disposables.add(boxModel);

            final Model boxw2Model = modelBuilder.createBox(2.2f, 1f, 1f, box2Material, attributes);
            disposables.add(boxw2Model);
            final Model boxw3Model = modelBuilder.createBox(3.3f, 1f, 1f, box2Material, attributes);
            disposables.add(boxw3Model);
            final Model boxw4Model = modelBuilder.createBox(4.4f, 1f, 1f, box2Material, attributes);
            disposables.add(boxw4Model);
            final Model boxw5Model = modelBuilder.createBox(5.5f, 1f, 1f, box2Material, attributes);
            disposables.add(boxw5Model);
            final Model boxw6Model = modelBuilder.createBox(6.6f, 1f, 1f, box2Material, attributes);
            disposables.add(boxw6Model);

            final Model bulletModel = modelBuilder.createSphere(0.6f, 0.6f, 0.6f, 24, 24, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                    ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), Usage.Position | Usage.Normal);
            disposables.add(bulletModel);

            // Add the constructors
            world.addConstructor("ground", new BulletConstructor(groundModel, 0f)); // mass = 0: static body
            world.addConstructor("box", new BulletConstructor(boxModel, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("box2w", new BulletConstructor(boxw2Model, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("box3w", new BulletConstructor(boxw3Model, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("box4w", new BulletConstructor(boxw4Model, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("box5w", new BulletConstructor(boxw5Model, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("box6w", new BulletConstructor(boxw6Model, 1f)); // mass = 1kg: dynamic body
            world.addConstructor("staticbox", new BulletConstructor(boxModel, 0f)); // mass = 0: static body
            world.addConstructor("bullet", new BulletConstructor(bulletModel, 1f)); // mass = 0: static body

            // ShootTest
            // Create the entities

            Color groundColor = new Color(0.6f, 0.6f, 0.6f, 1f);
            (ground = world.add("ground", 0f, 0f, 0f)).setColor(groundColor);

            FileHandle handle = Gdx.files.internal("data/physics/castle-1.txt");
            if (!handle.exists()) {
                throw new RuntimeException("physics layout not found: " + handle.path());
            }
            String layout = handle.readString();

            String[] row = layout.split("\\r?\\n");
            int zindex = 0;
            Integer rowheigths[] = findRowHeights(row);
            System.out.println("rowheigths: " + rowheigths.length);

            float zoffset = 0;
            int rowoffset = 0;
            for (int i = 0; i < row.length; i++) {
                // for each row
                char[] blocks = row[i].toCharArray();
                int longblock = 0;
                float xoffset = 0;
                float yoffset = 0 + (BOXOFFSET_Y * (rowheigths[zindex] - i) + rowoffset - 0.5f);
                for (int j = 0; j < blocks.length; j++) {
                    char block = blocks[j];

                    if (block != '=' && longblock > 0) {
                        addLongBlock(red, longblock, xoffset, yoffset, zoffset);
                        // xoffset += (1.1f * longblock);
                        longblock = 0;
                    }

                    switch (block) {
                    case '-':
                        System.out.println("current row offset " + zoffset + ", new rowheight: " + rowheigths[zindex]);
                        zoffset -= (BOXOFFSET_Z);
                        rowoffset += rowheigths[zindex] + 1;
                        zindex++;
                        System.out.println("next X row offset: " + zoffset + ", new rowheight: " + rowheigths[zindex]);
                        break;
                    case ' ':
                        xoffset += BOXOFFSET_X;
                        break;
                    case 'v':
                        // void
                        break;
                    case 'x':
                        System.out.println("adding box at " + xoffset + "/" + yoffset + "/" + zoffset);
                        world.add("box", BOXOFFSET_X + xoffset, yoffset, zoffset).setColor(blue);
                        if (xoffset == BOXOFFSET_X) {
                            targets.add(new Vector3(xoffset, yoffset, zoffset));
                        }
                        xoffset += BOXOFFSET_X;
                        break;
                    case '=':
                        // xoffset += BOXOFFSET_X;
                        longblock++;
                        xoffset += BOXOFFSET_X;
                        if (j == blocks.length - 1) {
                            addLongBlock(red, longblock, xoffset, yoffset, zoffset);
                        }
                        break;
                    default:
                        System.err.println("unknown building block: '" + block + "'");
                    }
                }

            }

            initialized = true;
            return true;
        } catch (Exception e) {
            Gdx.app.log("error", "PhysX not supported: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Integer[] findRowHeights(String[] row) {
        List<Integer> heights = new ArrayList();
        int last = 0;
        for (int i = 0; i < row.length; i++) {
            if (row[i].length() > 0) {
                if (row[i].charAt(0) == '-') {
                    heights.add(last);
                    last = 0;
                } else {
                    last++;
                }
            }
        }
        if (last > 0) {
            heights.add(last);
        }

        System.out.println(heights);

        return heights.toArray(new Integer[] {});
    }

    public void addLongBlock(Color red, int longblock, float xoffset, float yoffset, float zoffset) {
        System.out.println("adding largebox at " + xoffset + ", " + longblock + " long");
        String blockname;
        switch (longblock) {
        case 2:
            blockname = "box2w";
            break;
        case 3:
            blockname = "box3w";
            break;
        case 4:
            blockname = "box4w";
            break;
        case 5:
            blockname = "box5w";
            break;
        case 6:
            blockname = "box6w";
            break;
        default:
            blockname = "box";
            break;
        }
        world.add(blockname, xoffset - ((longblock - 1) * 0.55f), yoffset, zoffset).setColor(red);
    }

    @Override
    public void update(float dt) {
        // System.out.println("timelapse: " + (dt * 1000) + "ms");
    }

    @Override
    public void draw() {
        // bloom.capture();
        render(true);
        // bloom.render();
    }

    boolean shot1Fired, shot2Fired, shot3Fired;

    public void render(boolean update) {
        fpsCounter.put(Gdx.graphics.getFramesPerSecond());

        if (initialized) {
            if (startRenderTime == 0) {
                startRenderTime = System.currentTimeMillis();
                // shoot!
            }

            long renderTime = System.currentTimeMillis() - startRenderTime;
            if (!shot1Fired && renderTime > TimeUnit.SECONDS.toMillis(2)) {
                // shoot(655, 337);
                int targetsize = targets.size();
                for (int i = 0; i < targetsize; i++) {
                    shoot();
                }
                shot1Fired = true;
            }
            if (!shot2Fired && renderTime > TimeUnit.SECONDS.toMillis(4)) {
                for (int i = 0; i < targets.size(); i++) {
                    shoot();
                }
                shot2Fired = true;
            }
            if (!shot3Fired && renderTime > TimeUnit.SECONDS.toMillis(8)) {
                shot3Fired = true;
            }
            if (update) {
                update();
            }
            beginRender(true);

            renderWorld();
            lastRenderTime = System.currentTimeMillis();
            framesRendered++;
        }

        performance.setLength(0);
        performance.append("FPS: ").append(fpsCounter.value).append(", Physics engine: ").append((int) (performanceCounter.load.value * 100f)).append("%");
    }

    public void shoot() {
        Vector3 vector3 = targets.get(0);
        System.out.println("target: " + vector3);
        // Vector3 endPoint = camera.getPickRay(Gdx.graphics.getWidth() / 2 + Gdx.graphics.getWidth() / 2);
        shoot(vector3.x * (((float) Math.random() - 1) * 170) + 440, 230);
        targets.remove(0);
    }

    protected void renderWorld() {
        modelBatch.begin(camera);
        world.render(modelBatch, lights);
        modelBatch.end();
    }

    public void update() {
        world.update();
    }

    public BulletEntity shoot(final float x, final float y) {
        System.out.println("shooting at " + x + "," + y);
        return shoot(x, y, 30f);
    }

    public BulletEntity shoot(final float x, final float y, final float impulse) {
        return shoot("bullet", x, y, impulse);
    }

    public BulletEntity shoot(final String what, final float x, final float y, final float impulse) {
        // Shoot a box
        Ray ray = camera.getPickRay(x, y);
        System.out.println("ray: " + ray.origin);

        ray.origin.x -= 0.05f;
        ray.origin.y -= 0.40f;

        BulletEntity entity = world.add(what, ray.origin.x, ray.origin.y, ray.origin.z);
        entity.setColor(white);
        ((btRigidBody) entity.body).applyCentralImpulse(ray.direction.scl(impulse));
        return entity;
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {
        world.dispose();
        world = null;

        for (Disposable disposable : disposables)
            disposable.dispose();
        disposables.clear();

        modelBatch.dispose();
        modelBatch = null;

        // shadowBatch.dispose();
        shadowBatch = null;

        // shadowLight.dispose();
        shadowLight = null;
    }

    /** BaseBullet **/
    public BulletWorld createWorld() {
        return new BulletWorld();
    }

    /** input listener **/
    @Override
    public boolean longPress(float x, float y) {
        return true;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    /** ShootTest & GestureListener **/
    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public int getState() {
        return STATE;
    }

}
