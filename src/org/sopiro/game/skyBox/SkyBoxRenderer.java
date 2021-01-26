package org.sopiro.game.skyBox;

import java.lang.Math;

import org.joml.*;
import org.lwjgl.opengl.*;
import org.sopiro.game.entities.*;
import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.renderer.shader.*;
import org.sopiro.game.skyBox.*;
import org.sopiro.game.utils.*;

public class SkyBoxRenderer
{   
	private static final float SIZE = 1;
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};

	private static String[] PATHS = 
	{
		"skybox2/right.png",		
		"skybox2/left.png",		
		"skybox2/top.png",		
		"skybox2/bottom.png",		
		"skybox2/front.png",		
		"skybox2/back.png"	
	};
	
	private RawModel cube;
	private int texture;
	private SkyBoxShader shader;
	
	private float rotation = 0;
	private float rotationSpeed = 0.01f;
	
	public SkyBoxRenderer(Loader loader, Matrix4f projectionMatrix)
	{
		cube = loader.loadVAO(VERTICES, 3);
		texture = loader.loadCubeMap(PATHS);
		shader = new SkyBoxShader();
		setProjectionMatrix(projectionMatrix);
	}
	
	public void render(Camera camera, Vector3f fogColor, float brightness)
	{
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		shader.start();
		
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30(0);
		matrix.m31(0);
		matrix.m32(0);
		rotation += rotationSpeed;
		matrix.rotateY((float)Math.toRadians(rotation));
		if(rotation >= 360) rotation -= 360;
		
		shader.setViewMatrix(matrix);
		shader.setFogColor(fogColor);
		shader.setBrightness(brightness);
		GL30.glBindVertexArray(cube.getVaoID());
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL30.glBindVertexArray(0);
		shader.stop();
		GL11.glDepthFunc(GL11.GL_LESS);
	}
	
	public void setProjectionMatrix(Matrix4f projectionMatrix)
	{
		shader.start();
		shader.setProjectionMatrix(projectionMatrix);
		shader.stop();
	}
}
