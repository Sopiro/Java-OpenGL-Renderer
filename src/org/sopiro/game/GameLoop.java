package org.sopiro.game;

import java.lang.Math;
import java.util.*;
import java.util.Random;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.sopiro.game.animation.AnimatedModel;
import org.sopiro.game.animation.AnimationLoader;
import org.sopiro.game.entities.*;
import org.sopiro.game.entities.light.*;
import org.sopiro.game.guis.*;
import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.terrain.*;
import org.sopiro.game.texture.*;

public class GameLoop
{
    private Loader loader = new Loader();

    private MasterRenderer renderer = new MasterRenderer(loader);
    private GuiRenderer guiRenderer = new GuiRenderer(loader);

    private Random r = new Random();

    // Game Objects
    private Camera camera;
    private Entity e;
    private Entity e2;

    private List<Light> lights = new ArrayList<Light>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
    private List<GuiTexture> guis = new ArrayList<GuiTexture>();

    private int time;

    private AnimatedModel animatedModel;

    public GameLoop()
    {
        camera = new Camera();
    }

    public void init()
    {
        Light sun = new Sun(new Vector3f(100, 100, 100).mul(-1));
        lights.add(sun);
        lights.add(new PointLight(new Vector3f(-50, 10, -50), new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), new Vector3f(10, 10, 10), new Vector3f(1f, 0.01f, 0.01f), 200));
        lights.add(new PointLight(new Vector3f(50, 10, 50), new Vector3f(0, 0, 5), new Vector3f(1, 0.01f, 0.019f), 200));

        TexturedModel model = new TexturedModel(AssimpLoader.load(loader, "runner.dae"), loader.runner);
        TexturedModel model2 = new TexturedModel(OBJLoader.loadObjModel(loader, "cube"), loader.brick3);

        e = new Entity(model, new Vector3f(0, 40, 0), new Vector3f(-90, 0, 0), 10);
        e2 = new Entity(model2, new Vector3f(-100, 100, 100), new Vector3f(), 30);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrainPlane.png").getID());
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud.png").getID());
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers.png").getID());
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path.png").getID());
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("black.png").getID());

        for (int i = 0; i < 4; i++)
            terrains.add(new Terrain(i / 2 - 1, i % 2 - 1, loader, texturePack, blendMap));

        GuiTexture crosshair = new GuiTexture(loader.loadTexture("crosshair.png").getID(), new Vector2f(0.0f, 0.0f), new Vector2f(0.025f));
        guis.add(crosshair);

        animatedModel = AnimationLoader.load(loader, "runner.dae");

        // camera.setPosition(new Vector3f(-100, 100, 100));
    }

    public void update()
    {
        time++;
        camera.update();

        float x = (float) Math.cos(time * 0.005) * 30;
        float z = (float) Math.sin(time * 0.005) * 30;

        if (Input.isDown(GLFW.GLFW_KEY_E))
            lights.get(1).setPosition(new Vector3f(camera.getPosition()).add(0, 0, -0));

//        e.rotate(0, x * 0.01f, 0);
//        e2.rotate(x * 0.01f, z * 0.01f, x * 0.01f);

        float angle = MasterRenderer.FOV / 2.5f;
        float distance = 300;
        float h = (float) Math.tan(angle) * distance;
        float w = h * Window.getWindowAspectRatio();
    }

    public void render(float offset)
    {
//        renderer.processEntity(e);
//        renderer.processEntity(e2);
        renderer.processAnimatedModel(animatedModel);
        renderer.processTerrain(terrains);
        renderer.render(lights, camera);

        guiRenderer.render(guis);
    }

    public void terminate()
    {
        renderer.terminate();
        guiRenderer.terminate();
        loader.terminate();
    }
}
