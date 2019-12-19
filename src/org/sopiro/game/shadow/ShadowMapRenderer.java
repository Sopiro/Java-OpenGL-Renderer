package org.sopiro.game.shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.*;
import org.sopiro.game.*;
import org.sopiro.game.entities.*;
import org.sopiro.game.entities.light.*;
import org.sopiro.game.models.*;
import org.sopiro.game.utils.*;

/**
 * @author Sopiro (https://goo.gl/1WFWq1)
 *
 */
public class ShadowMapRenderer
{
	private ShadowMapShader shader;

	private int width;
	private int height;

	private int shadowMapFBO;
	private int shadowMap;

	private Matrix4f lightSpaceMatrix;

	private ShadowBox shadowBox;

	public ShadowMapRenderer(int shadowMapWidth, int shadowMapHeight, float shadowDistance, float shadowOffset)
	{
		this.width = shadowMapWidth;
		this.height = shadowMapHeight;

		shadowMapFBO = glGenFramebuffers();
		shadowMap = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, shadowMap);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] { 1, 1, 1, 1 });
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, shadowMapFBO);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMap, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		shader = new ShadowMapShader();
		shadowBox = new ShadowBox(shadowDistance, shadowOffset);
	}

	public void render(Camera camera, Sun light, Map<TexturedModel, List<Entity>> entities)
	{
		glViewport(0, 0, width, height);
		glBindFramebuffer(GL_FRAMEBUFFER, shadowMapFBO);
		glClear(GL_DEPTH_BUFFER_BIT);
//		glCullFace(GL_FRONT);
		shader.start();

		Matrix4f lightSight = new Matrix4f();
		lightSight.lookAt(Maths.sum(camera.getPosition(), light.getPosition()), camera.getPosition(), new Vector3f(0, 1, 0));
		shadowBox.update(lightSight, camera);
		Matrix4f ortho = new Matrix4f().ortho(shadowBox.getMinX(), shadowBox.getMaxX(), shadowBox.getMinY(), shadowBox.getMaxY(), shadowBox.getMinZ(), shadowBox.getMaxZ());

		lightSpaceMatrix = ortho.mul(lightSight);

		shader.setVPMatrix(lightSpaceMatrix);
		for (TexturedModel t : entities.keySet())
		{
			RawModel model = t.getRawModel();
			glBindVertexArray(model.getVaoID());
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, t.getMaterial().getDiffuseMap().getID());
			
			List<Entity> e = entities.get(t);

			for (Entity entity : e)
			{
				Matrix4f transformation = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
				shader.setMMatrix(transformation);
				glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
			}
		}
		shader.stop();
//		glCullFace(GL_BACK);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.getWindowWidth(), Window.getWindowHeight());
	}

	public int getShadowMap()
	{
		return shadowMap;
	}

	public Matrix4f getLightSpaceMatrix()
	{
		return lightSpaceMatrix;
	}

	public int getShadowMapWidth()
	{
		return width;
	}

	public void setShadowMapWidth(int width)
	{
		this.width = width;
	}

	public int getShadowMapHeight()
	{
		return height;
	}

	public void setShadowBoxDistance(float distance)
	{
		shadowBox.setDistance(distance);
	}

	public void setShadowBoxOffset(float offset)
	{
		shadowBox.setOffset(offset);
	}

	public void setShadowMapHeight(int height)
	{
		this.height = height;
	}

	public void terminate()
	{
		glDeleteFramebuffers(shadowMapFBO);
		glDeleteTextures(shadowMap);
		shader.terminate();
	}
}
