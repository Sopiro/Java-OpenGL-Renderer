package org.sopiro.game.renderer;

import static org.lwjgl.opengl.GL11.*;

import java.lang.Math;
import java.util.*;

import org.joml.*;
import org.sopiro.game.*;
import org.sopiro.game.animation.AnimatedModel;
import org.sopiro.game.animation.AnimationRenderer;
import org.sopiro.game.animation.AnimationShader;
import org.sopiro.game.entities.*;
import org.sopiro.game.entities.light.*;
import org.sopiro.game.models.*;
import org.sopiro.game.postProcess.*;
import org.sopiro.game.shadow.*;
import org.sopiro.game.skyBox.*;
import org.sopiro.game.terrain.*;
import org.sopiro.game.texture.*;
import org.sopiro.game.utils.*;

public class MasterRenderer
{
    public static final float FOV = (float) Math.toRadians(87);
    public static final float NEAR_PLANE = 0.1f; //(float) (1.0 / Math.tan(FOV/2));
    public static final float FAR_PLANE = 2000f;
    private static final Vector3f FOG_COLOR = new Vector3f(185 / 255.0f, 223 / 255.0f, 253 / 255.0f);
    private Matrix4f projectionMatrix;
    private float time = 0;
    private boolean postProcessEnabled = true;

    private PostProcesser postProcesser;
    private MultiSampleFrameBuffer msaa;

    private EntityShader entityShader = new EntityShader();
    private EntityRenderer entityRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;
    private SkyBoxRenderer skyboxRenderer;
    private ShadowMapRenderer shadowMapRenderer;
    private int shadowMapSize = 6000;
    private int shadowDistance = 500;
    private int shadowOffset = 300;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    private AnimationShader animationShader = new AnimationShader();
    private AnimationRenderer animationRenderer;
    private AnimatedModel animatedModel;

    public MasterRenderer(Loader loader)
    {
        GLUtills.initOpenGLSettings();
        projectionMatrix = createProjectionMatrix();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyBoxRenderer(loader, projectionMatrix);
        shadowMapRenderer = new ShadowMapRenderer(shadowMapSize, shadowMapSize, shadowDistance, shadowOffset);

        animationRenderer = new AnimationRenderer(animationShader, projectionMatrix);

        msaa = new MultiSampleFrameBuffer();
        postProcesser = new PostProcesser(loader);
    }

    public static void enableCulling()
    {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableCulling()
    {
        glDisable(GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera)
    {
        time++;

        Sun sun = (Sun) lights.get(0);

        shadowMapRenderer.render(camera, sun, entities);
        Texture shadowMap = new Texture(shadowMapRenderer.getShadowMap());
        Matrix4f lightSpaceMatrix = shadowMapRenderer.getLightSpaceMatrix();

        if (postProcessEnabled)
            msaa.bind();
        prepare();

        terrainShader.start();
        terrainShader.setSkyColor(FOG_COLOR);
        terrainShader.setLights(lights);
        terrainShader.setViewMatrix(camera);
        terrainRenderer.setShadowMap(shadowMap, lightSpaceMatrix, shadowMapSize);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        entityShader.start();
        entityShader.updateTime(time);
        entityShader.setSkyColor(FOG_COLOR);
        entityShader.setLights(lights);
        entityShader.setViewMatrix(camera);
        entityRenderer.setShadowMap(shadowMap, lightSpaceMatrix, shadowMapSize);
        entityRenderer.render(entities);
        entityShader.stop();

        animationShader.start();
        animationShader.setViewMatrix(camera);
        animationShader.setTransformationMatrix(Maths.createTransformationMatrix(
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 0),
                10));
        animationRenderer.render(animatedModel);
        animationShader.stop();

        skyboxRenderer.render(camera, FOG_COLOR, 2.5f);

        terrains.clear();
        entities.clear();

        if (postProcessEnabled)
        {
            msaa.unbind();
            int screenTexture = msaa.getScreenTexure();

            postProcesser.postProcess(screenTexture);
        }
    }

    private void prepare()
    {
        glClearColor(FOG_COLOR.x, FOG_COLOR.y, FOG_COLOR.z, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void processTerrain(Terrain terrain)
    {
        terrains.add(terrain);
    }

    public void processTerrain(List<Terrain> terrains)
    {
        this.terrains.addAll(terrains);
    }

    public void processEntity(Entity entity)
    {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null)
        {
            batch.add(entity);
        } else
        {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void terminate()
    {
        shadowMapRenderer.terminate();
        msaa.terminate();
        postProcesser.terminate();
        entityShader.terminate();
        terrainShader.terminate();
        animationShader.terminate();
    }

    public void zoom(float zoom)
    {
        this.projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(zoom), Window.getWindowAspectRatio(), NEAR_PLANE, FAR_PLANE);
        entityRenderer.setProjectionMatrix(projectionMatrix);
        terrainRenderer.setProjectionMatrix(projectionMatrix);
    }

    public void resetZoom()
    {
        zoom(FOV);
    }

    private Matrix4f createProjectionMatrix()
    {
//		return new Matrix4f().ortho(-100, 100, -100, 100, 0.1f, 1000);
        return new Matrix4f().perspective(FOV, Window.getWindowAspectRatio(), NEAR_PLANE, FAR_PLANE);
    }

    public void processAnimatedModel(AnimatedModel animatedModel)
    {
        this.animatedModel = animatedModel;
    }

    public void togglePostProcessEnabled()
    {
        if (postProcessEnabled)
        {
            setPostProcessEnabled(false);
        } else
            setPostProcessEnabled(true);
    }

    public void setPostProcessEnabled(boolean doPostProcess)
    {
        this.postProcessEnabled = doPostProcess;
    }
}
