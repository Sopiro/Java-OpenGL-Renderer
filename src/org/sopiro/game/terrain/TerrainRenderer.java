package org.sopiro.game.terrain;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.*;
import org.sopiro.game.texture.*;
import org.sopiro.game.utils.*;

public class TerrainRenderer
{
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		setProjectionMatrix(projectionMatrix);
	}
	
	public void render(List<Terrain> terrains)
	{
		for(Terrain terrain : terrains)
		{
			prepareTerrain(terrain);
			setModelMatrix(terrain);
			
			glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			
			unbindTerrain();
		}
	}

	public void setShadowMap(Texture shadowMap, Matrix4f lightSpaceMatrix, int shadowMapSize)
	{
		glActiveTexture(GL_TEXTURE5);
		glBindTexture(GL_TEXTURE_2D, shadowMap.getID());
		shader.setLightSpaceMatrix(lightSpaceMatrix);
		shader.setShadowMapSize(shadowMapSize);
	}
	
	private void prepareTerrain(Terrain terrain)
	{
		bindTextures(terrain);
		glBindVertexArray(terrain.getModel().getVaoID());
		shader.setMaterial(terrain.getTexturePack());
	}
	
	private void bindTextures(Terrain terrain)
	{
		TerrainTexturePack texturePack = terrain.getTexturePack();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getID());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, texturePack.getrTexture().getID());
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, texturePack.getgTexture().getID());
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, texturePack.getbTexture().getID());
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getID());
	}
	
	private void unbindTerrain()
	{
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private void setModelMatrix(Terrain terrain)
	{
		Matrix4f transformation = Maths.createTransformationMatrix(new Vector3f(terrain.getWorldX(), 0, terrain.getWorldZ()), new Vector3f(), 1);
		shader.setTransformationMatrix(transformation);
	}
	
	public void setProjectionMatrix(Matrix4f projectionMatrix)
	{
		shader.start();
		shader.setProjectionMatrix(projectionMatrix);
		shader.stop();
	}
}
