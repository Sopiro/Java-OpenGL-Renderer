package org.sopiro.game.entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.*;
import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.texture.*;
import org.sopiro.game.utils.*;

public class EntityRenderer
{
    private EntityShader shader;

    public EntityRenderer(EntityShader shader, Matrix4f projectionMatrix)
    {
        this.shader = shader;
        setProjectionMatrix(projectionMatrix);
    }

    public void render(Map<TexturedModel, List<Entity>> entities)
    {
        for(TexturedModel model : entities.keySet())
        {
            bindTexturedModel(model);
            List<Entity> batch = entities.get(model);

            if(model.getMaterial().isHasNormalMap())
            {
                shader.setNormalMappingUsed(true);
                bindNormalMap(model);
            }

            if(model.getMaterial().isHasDepthMap())
            {
                shader.setParallaxMappingUsed(true);
                bindDepthMap(model);
            }

            for(Entity entity : batch)
            {
                if(entity.isOutLined())
                {
                    glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
                    bindEntityAttributes(entity);
                    glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);

                    glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
                    glStencilFunc(GL_NOTEQUAL, 1, 0xff);
                    float scale = entity.getScale();
                    entity.setScale(scale * 1.01f);
                    bindEntityAttributes(entity);
                    shader.setOutlined(true);
//					glDisable(GL_DEPTH_TEST);
                    glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);

//					glEnable(GL_DEPTH_TEST);
                    entity.setScale(scale);
                    shader.setOutlined(false);
                    glStencilFunc(GL_ALWAYS, 1, 0xff);
                }
                else
                {
                    bindEntityAttributes(entity);
                    glDrawElements(GL_TRIANGLES, model.getRawModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                }
            }
            unbindTexturedModel();
            shader.setNormalMappingUsed(false);
            shader.setParallaxMappingUsed(false);
        }
    }

    private void bindNormalMap(TexturedModel model)
    {
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, model.getMaterial().getNormalMap().getID());
    }

    private void bindDepthMap(TexturedModel model)
    {
        glActiveTexture(GL_TEXTURE7);
        glBindTexture(GL_TEXTURE_2D, model.getMaterial().getDepthMap().getID());
    }

    private void bindTexturedModel(TexturedModel model)
    {
        Material material = model.getMaterial();
        if(material.isHasTransparency())
        {
            MasterRenderer.disableCulling();
        }
        glBindVertexArray(model.getRawModel().getVaoID());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, material.getDiffuseMap().getID());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, material.getSpecularMap().getID());
        shader.setUseFakeLighting(material.isUseFakeLighting());
        shader.setMaterialVariables(material);
    }

    private void bindEntityAttributes(Entity entity)
    {
        Matrix4f transformation = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
        shader.setTransformationMatrix(transformation);
    }

    public void setShadowMap(Texture shadowMap, Matrix4f lightSpaceMatrix, int shadowMapSize)
    {
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getID());
        shader.setLightSpaceMatrix(lightSpaceMatrix);
        shader.setShadowMapSize(shadowMapSize);
    }

    private void unbindTexturedModel()
    {
        MasterRenderer.enableCulling();
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix)
    {
        shader.start();
        shader.setProjectionMatrix(projectionMatrix);
        shader.stop();
    }
}
